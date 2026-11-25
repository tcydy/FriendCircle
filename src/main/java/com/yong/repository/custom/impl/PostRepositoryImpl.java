package com.yong.repository.custom.impl;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.aggregation.VariableOperators;

import com.mongodb.client.result.UpdateResult;
import com.yong.pojo.Follow;
import com.yong.pojo.LikeResult;
import com.yong.pojo.Post;
import com.yong.pojo.Post.PostComment;
import com.yong.repository.custom.PostCustomRepository;

@Repository
public class PostRepositoryImpl implements PostCustomRepository {

        private final MongoTemplate mongoTemplate;

        PostRepositoryImpl(MongoTemplate mongoTemplate) {
                this.mongoTemplate = mongoTemplate;
        }

        @Override
        public List<Post> findSquarePostByCursor(Integer size, LocalDateTime lastCreatedAt, String currentUserId) {
                // 初始匹配：直接排除私密 status=1
                Criteria matchCriteria = Criteria.where("status").ne(1);
                if (lastCreatedAt != null) {
                        matchCriteria.and("createdAt").lt(lastCreatedAt);
                }

                Aggregation agg = Aggregation.newAggregation(
                                // 1. 基础过滤（排除私密、时间游标）
                                Aggregation.match(matchCriteria),

                                // 2. 关联 follow 集合（Bug 修复：加入 followerId = currentUserId 限制）
                                Aggregation.lookup()
                                                .from("follow")
                                                .let(VariableOperators.Let.just(
                                                                VariableOperators.Let.ExpressionVariable
                                                                                .newVariable("authorId")
                                                                                .forField("authorId")))
                                                .pipeline(
                                                                Aggregation.match(
                                                                                new Criteria().andOperator(
                                                                                                Criteria.where("followeeId")
                                                                                                                .is("$$authorId"),
                                                                                                Criteria.where("followerId")
                                                                                                                .is(currentUserId) // 只查当前用户的关注记录
                                                                                )))
                                                .as("followRecord"),

                                // 3. 计算 canView 权限
                                Aggregation.addFields()
                                                .addField("canView")
                                                .withValueOfExpression("""
                                                                $or: [
                                                                    {$eq: ["$status", 0]},
                                                                    {$and: [
                                                                        {$eq: ["$status", 2]},
                                                                        {$gt: [{$size: "$followRecord"}, 0]}
                                                                    ]},
                                                                    {$eq: ["$authorId", ?0]}
                                                                ]
                                                                """, currentUserId)
                                                .build(),

                                // 4. 过滤没有权限的帖子
                                Aggregation.match(Criteria.where("canView").is(true)),

                                // 5. 投影
                                Aggregation.project()
                                                .andInclude("id", "authorId", "authorInfo", "title", "content",
                                                                "images", "location",
                                                                "likesNum", "status", "createdAt"),

                                // 6. 按发布时间倒序
                                Aggregation.sort(Sort.Direction.DESC, "createdAt"),

                                // 7. 限制条数
                                Aggregation.limit(size));

                AggregationResults<Post> result = mongoTemplate.aggregate(agg, "post", Post.class);
                return result.getMappedResults();
        }

        @Override
        public List<Post> findFollowingPostByCursor(Integer size, LocalDateTime lastCreatedAt, String currentUserId) {

                // 0. 参数保护：限制单页上限，防止大查询拖垮 DB
                int limit = (size == null || size <= 0) ? 10 : Math.min(size, 50);

                // 1. 初始匹配：排除私密 status=1，并按游标截断
                Criteria matchCriteria = Criteria.where("status").ne(1);
                if (lastCreatedAt != null) {
                        matchCriteria.and("createdAt").lt(lastCreatedAt);
                }

                Aggregation agg = Aggregation.newAggregation(
                                // 阶段1：过滤私密 + 游标（最便宜的过滤先做）
                                Aggregation.match(matchCriteria),

                                // 阶段2：先按时间倒序，减少后续 lookup 的数据量
                                Aggregation.sort(Sort.Direction.DESC, "createdAt"),

                                // 阶段3：pipeline 形式 lookup，只关联"当前用户关注该作者"的记录
                                Aggregation.lookup()
                                                .from("follow")
                                                // 使用 VariableOperators.Let 构建变量，解决报错
                                                .let(VariableOperators.Let.just(
                                                                VariableOperators.Let.ExpressionVariable
                                                                                .newVariable("authorId")
                                                                                .forField("authorId")))
                                                // 内部管道匹配条件
                                                .pipeline(
                                                                Aggregation.match(
                                                                                new Criteria().andOperator(
                                                                                                Criteria.where("followeeId")
                                                                                                                .is("$$authorId"), // 注意使用
                                                                                                                                   // $$
                                                                                                                                   // 引用变量
                                                                                                Criteria.where("followerId")
                                                                                                                .is(currentUserId))))
                                                .as("followRecord"),

                                // 阶段4：计算 isFollowOrSelf = 自己 OR 我关注了作者
                                Aggregation.addFields()
                                                .addField("isFollowOrSelf")
                                                .withValueOfExpression("""
                                                                $or: [
                                                                    {$eq: ["$authorId", ?0]},
                                                                    {$gt: [{$size: "$followRecord"}, 0]}
                                                                ]
                                                                """, currentUserId)
                                                .build(),

                                // 阶段5：只保留自己或关注人的帖子
                                Aggregation.match(Criteria.where("isFollowOrSelf").is(true)),

                                // 阶段6：投影，白名单只保留业务字段（直接抛弃 followRecord 和 isFollowOrSelf）
                                Aggregation.project()
                                                .andInclude("id", "authorId", "authorInfo", "title", "content",
                                                                "images",
                                                                "location", "likesNum", "status", "createdAt"),

                                // 阶段7：游标分页截断（必须在过滤之后）
                                Aggregation.limit(limit));

                AggregationResults<Post> result = mongoTemplate.aggregate(agg, "post", Post.class);
                return result.getMappedResults();

        }

        @Override
        public List<Post> findNearbyPostByCursor(Integer size, LocalDateTime lastCreatedAt, double distance,
                        String currentUserId, double x, double y) {
                // 1. 基础过滤条件（排除私密 status=1，以及时间游标）
                Criteria matchCriteria = Criteria.where("status").ne(1);
                if (lastCreatedAt != null) {
                        matchCriteria.and("createdAt").lt(lastCreatedAt);
                }

                Query query = new Query(matchCriteria);

                // 2. 构建地理空间查询 NearQuery
                // 注意：MongoDB 中坐标的顺序是 [经度, 纬度]，即 [x, y]
                Point point = new Point(x, y);
                NearQuery nearQuery = NearQuery.near(point)
                                .maxDistance(distance) // 距离范围（米）
                                .spherical(true) // 球面计算
                                .query(query); // 将基础过滤条件注入

                Aggregation agg = Aggregation.newAggregation(
                                // 阶段1：$geoNear (必须是管道的第一个阶段)
                                // "distance" 是自动生成的字段，代表计算出的距离，返回给前端用
                                Aggregation.geoNear(nearQuery, "distance"),

                                // 阶段2：关联 follow 集合 (followerId 限制)
                                Aggregation.lookup()
                                                .from("follow")
                                                .let(VariableOperators.Let.just(
                                                                VariableOperators.Let.ExpressionVariable
                                                                                .newVariable("authorId")
                                                                                .forField("authorId")))
                                                .pipeline(
                                                                Aggregation.match(
                                                                                new Criteria().andOperator(
                                                                                                Criteria.where("followeeId")
                                                                                                                .is("$$authorId"),
                                                                                                Criteria.where("followerId")
                                                                                                                .is(currentUserId))))
                                                .as("followRecord"),

                                // 阶段3：计算 canView 权限
                                Aggregation.addFields()
                                                .addField("canView")
                                                .withValueOfExpression("""
                                                                $or: [
                                                                    {$eq: ["$status", 0]},
                                                                    {$and: [
                                                                        {$eq: ["$status", 2]},
                                                                        {$gt: [{$size: "$followRecord"}, 0]}
                                                                    ]},
                                                                    {$eq: ["$authorId", ?0]}
                                                                ]
                                                                """, currentUserId)
                                                .build(),

                                // 阶段4：过滤没有权限的帖子
                                Aggregation.match(Criteria.where("canView").is(true)),

                                // 阶段5：投影 (修复：andInclude 自动排除了未列出的字段，不能和 andExclude 混用)
                                // 注意加上 "distance"，让前端能获取到距离
                                Aggregation.project()
                                                .andInclude("id", "authorId", "authorInfo", "title", "content",
                                                                "images", "location",
                                                                "likesNum", "status", "createdAt", "distance"),

                                // 阶段6：按发布时间倒序 (覆盖 $geoNear 默认的按距离排序)
                                Aggregation.sort(Sort.Direction.DESC, "createdAt"),

                                // 阶段7：限制返回条数
                                Aggregation.limit(size));

                AggregationResults<Post> result = mongoTemplate.aggregate(agg, "post", Post.class);
                return result.getMappedResults();

        }

        @Override
        public List<Post> search(Integer size, LocalDateTime lastCreatedAt, String userId, String keyword) {
                // 1. 初始匹配：排除私密 status=1，并增加标题关键词模糊匹配
                Criteria matchCriteria = Criteria.where("title").regex(keyword, "i")
                                .and("status").ne(1);

                // 时间游标
                if (lastCreatedAt != null) {
                        matchCriteria.and("createdAt").lt(lastCreatedAt);
                }

                Aggregation agg = Aggregation.newAggregation(
                                // 1. 基础过滤（关键词匹配、排除私密、时间游标）
                                Aggregation.match(matchCriteria),

                                // 2. 关联 follow 集合（加入 followerId = currentUserId 限制）
                                Aggregation.lookup()
                                                .from("follow")
                                                .let(VariableOperators.Let.just(
                                                                VariableOperators.Let.ExpressionVariable
                                                                                .newVariable("authorId")
                                                                                .forField("authorId")))
                                                .pipeline(
                                                                Aggregation.match(
                                                                                new Criteria().andOperator(
                                                                                                Criteria.where("followeeId")
                                                                                                                .is("$$authorId"),
                                                                                                Criteria.where("followerId")
                                                                                                                .is(userId) // 只查当前用户的关注记录
                                                                                )))
                                                .as("followRecord"),

                                // 3. 计算 canView 权限
                                Aggregation.addFields()
                                                .addField("canView")
                                                .withValueOfExpression("""
                                                                $or: [
                                                                    {$eq: ["$status", 0]},
                                                                    {$and: [
                                                                        {$eq: ["$status", 2]},
                                                                        {$gt: [{$size: "$followRecord"}, 0]}
                                                                    ]},
                                                                    {$eq: ["$authorId", ?0]}
                                                                ]
                                                                """, userId)
                                                .build(),

                                // 4. 过滤没有权限的帖子
                                Aggregation.match(Criteria.where("canView").is(true)),

                                // 5. 投影
                                Aggregation.project()
                                                .andInclude("id", "authorId", "authorInfo", "title", "content",
                                                                "images", "location",
                                                                "likesNum", "status", "createdAt"),

                                // 6. 按发布时间倒序
                                Aggregation.sort(Sort.Direction.DESC, "createdAt"),

                                // 7. 限制条数
                                Aggregation.limit(size));

                AggregationResults<Post> result = mongoTemplate.aggregate(agg, "post", Post.class);
                return result.getMappedResults();
        }

        @Override
        public LikeResult like(String postId, String userId, Integer action) {
                // 点赞 (0)
                if (action != null && action == 0) {
                        // 条件：帖子ID匹配，且 likes 集合中【不包含】该用户
                        Query query = Query.query(
                                        Criteria.where("id").is(postId).and("likes").ne(userId));

                        Update update = new Update()
                                        .addToSet("likes", userId)
                                        .inc("likesNum", 1);

                        // 使用 findAndModify，并返回更新后的文档
                        Post updatedPost = mongoTemplate.findAndModify(
                                        query,
                                        update,
                                        FindAndModifyOptions.options().returnNew(true),
                                        Post.class);

                        // 如果 updatedPost 不为空，说明更新成功
                        if (updatedPost != null) {
                                return new LikeResult(updatedPost.getLikesNum(), true);
                        }
                }
                // 取消点赞 (1)
                else if (action != null && action == 1) {
                        // 条件：帖子ID匹配，且 likes 集合中【包含】该用户
                        Query query = Query.query(
                                        Criteria.where("id").is(postId).and("likes").is(userId));

                        Update update = new Update()
                                        .pull("likes", userId)
                                        .inc("likesNum", -1);

                        Post updatedPost = mongoTemplate.findAndModify(
                                        query,
                                        update,
                                        FindAndModifyOptions.options().returnNew(true),
                                        Post.class);

                        if (updatedPost != null) {
                                return new LikeResult(updatedPost.getLikesNum(), false);
                        }
                }

                // ================= 兜底处理（高并发/重复点击情况） =================
                // 走到这里说明：用户重复点赞了，或者没点赞就取消，或者帖子不存在
                // 为了前端的状态依然能同步，我们去数据库查一下当前的真实状态返回给前端
                Post currentPost = mongoTemplate.findOne(Query.query(Criteria.where("id").is(postId)), Post.class);
                if (currentPost == null) {
                        throw new RuntimeException("帖子不存在"); // 或者抛出自定义业务异常
                }

                boolean isLiked = currentPost.getLikes() != null && currentPost.getLikes().contains(userId);
                return new LikeResult(currentPost.getLikesNum(), isLiked);
        }

        @Override
        public List<Post> findMyPostByCursor(Integer size, LocalDateTime lastCreatedAt, String myId, String userId) {
                // 0. 分页参数兜底，Aggregation.limit(null) 会 NPE
                int limit = (size == null || size <= 0) ? 10 : Math.min(size, 50);

                // 1. 公共过滤：作者 + 时间游标
                Criteria matchCriteria = Criteria.where("authorId").is(userId);
                if (lastCreatedAt != null) {
                        matchCriteria.and("createdAt").lt(lastCreatedAt);
                }

                // 2. 可见性：自己看自己 → 不过滤；看别人 → 公开 + (粉丝可见)
                if (!Objects.equals(myId, userId)) {
                        // 单向判断：访问者 myId 是否关注了作者 userId
                        boolean followed = mongoTemplate.exists(
                                        Query.query(Criteria.where("followerId").is(myId)
                                                        .and("followeeId").is(userId)),
                                        Follow.class);

                        if (followed) {
                                matchCriteria.and("status").in(0, 2); // 公布 + 仅粉丝可见
                        } else {
                                matchCriteria.and("status").is(0); // 只剩公布，私密和仅粉丝可见都被过滤
                        }
                }

                // 3. 管道
                Aggregation agg = Aggregation.newAggregation(
                                Aggregation.match(matchCriteria),

                                Aggregation.project()
                                                .andInclude("_id", "authorId", "authorInfo", "title", "content",
                                                                "images", "location", "likesNum", "likes", "status",
                                                                "createdAt"),

                                // _id 作为第二排序键，避免同毫秒发布时顺序抖动
                                Aggregation.sort(Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("_id"))),

                                Aggregation.limit(limit));

                AggregationResults<Post> result = mongoTemplate.aggregate(agg, "post", Post.class);
                return result.getMappedResults();
        }

        @Override
        public boolean addComment(String id, PostComment comment) {
                // 1. 查询条件：帖子 ID 匹配
                Query query = Query.query(Criteria.where("id").is(id));

                // 2. 更新操作：向 comments 数组 push 一条新评论
                Update update = new Update().push("comments", comment);

                // 3. 执行更新
                UpdateResult result = mongoTemplate.updateFirst(query, update, Post.class);

                // 4. 判断是否匹配并修改了数据
                return result.getMatchedCount() > 0;
        }

        @Override
        public Boolean deleteComment(String userId, String id, String commentId) {
                // 1. 构建极其严谨的查询条件
                Query query = Query.query(
                                Criteria.where("id").is(id)
                                                // 权限校验：要么当前人是贴主，要么当前人是评论发布者
                                                .orOperator(
                                                                Criteria.where("authorId").is(userId),
                                                                Criteria.where("comments").elemMatch(
                                                                                Criteria.where("id").is(commentId)
                                                                                                .and("userId")
                                                                                                .is(userId))));

                // 2. 构建更新操作：从 comments 数组中移除指定 id 的评论
                Update update = new Update().pull("comments", Query.query(Criteria.where("id").is(commentId)));

                // 3. 执行更新
                UpdateResult result = mongoTemplate.updateFirst(query, update, Post.class);

                // 4. 如果 modifiedCount > 0，说明条件匹配并且成功删除了评论
                return result.getModifiedCount() > 0;
        }

}
