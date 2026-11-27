package com.yong.repository.custom.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.bson.Document;
import org.springframework.data.domain.Sort;

import org.springframework.data.mongodb.core.aggregation.*;

import com.yong.pojo.User;
import com.yong.repository.custom.UserCustomRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserCustomRepository {

    private final MongoTemplate mongoTemplate;

    UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // 根据id更新用户信息
    @Override
    public void updateProfileById(User user) {
        // 条件
        Query query = Query.query(Criteria.where("id").is(user.getId()));

        // 要更新的字段
        Update update = new Update()
                .set("nickname", user.getNickname())
                .set("profile", user.getProfile())
                .set("avatar", user.getAvatar());

        // 执行更新：更新第一条匹配的记录
        mongoTemplate.updateFirst(query, update, User.class);
    }

    // 根据id更新密码
    @Override
    public void updatePasswordById(User user) {
        // 条件
        Query query = Query.query(Criteria.where("id").is(user.getId()));

        // 要更新的字段
        Update update = new Update()
                .set("password", user.getPassword());

        // 执行更新：更新第一条匹配的记录
        mongoTemplate.updateFirst(query, update, User.class);
    }

    // 根据id更新位置
    @Override
    public void updateLocationById(String id, Double longitude, Double latitude) {
        GeoJsonPoint location = new GeoJsonPoint(longitude, latitude);

        // 条件
        Query query = Query.query(Criteria.where("id").is(id));

        // 要更新的字段
        Update update = new Update()
                .set("location", location);

        // 执行更新：更新第一条匹配的记录
        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public boolean isUsernameExist(String username) {
        // 1. 构建查询条件：username 等于传入的值
        Query query = Query.query(Criteria.where("username").is(username));

        // 2. 调用 exists 方法直接判断是否存在
        return mongoTemplate.exists(query, User.class);
    }

    // 更新统计数据（使用 $inc 保证原子性）
    @Override
    public void updateStats(String userId, int followingDelta, int followerDelta) {
        Query query = Query.query(Criteria.where("id").is(userId));

        Update update = new Update();
        if (followingDelta != 0) {
            update.inc("stats.followingCount", followingDelta);
        }
        if (followerDelta != 0) {
            update.inc("stats.followerCount", followerDelta);
        }

        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public List<User> relations(String safeKeyword, Integer safeSize, LocalDateTime lastCreatedAt,
            String userId, Integer type) {

        log.info("relations 入参: keyword=[{}] len={} type={} userId={} lastCreatedAt={} safeSize={}",
                safeKeyword,
                safeKeyword == null ? -1 : safeKeyword.length(),
                type, userId, lastCreatedAt, safeSize);

        int relationType = (type == null) ? 0 : type;
        if (relationType != 0 && relationType != 1) {
            throw new IllegalArgumentException("type 只能是 0（我关注的人）或 1（我的粉丝）");
        }
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("userId 不能为空");
        }

        // type=0：我关注的人 -> follower_id = 我，取 followee_id 对应用户
        // type=1：我的粉丝   -> followee_id = 我，取 follower_id 对应用户
        String selfField = (relationType == 0) ? "follower_id" : "followee_id";
        String targetField = (relationType == 0) ? "followee_id" : "follower_id";

        // 1. 关系过滤 + 时间游标
        //    注意：and() 返回新实例，必须用 andOperator 组合，否则游标条件会被丢弃
        Criteria criteria = Criteria.where(selfField).is(userId.trim());
        if (lastCreatedAt != null) {
            criteria = new Criteria().andOperator(
                    criteria,
                    Criteria.where("created_at").lt(lastCreatedAt));
        }
        log.info("relations 关系过滤条件: {}", criteria.getCriteriaObject());

        // 2. 用户侧过滤：关键词已在 service 层转义，这里只判空、直接使用
        //    $lookup 未命中时 targetUser 为 []，必须用 targetUser.0 判断
        Criteria userCriteria = Criteria.where("targetUser.0").exists(true);
        if (StringUtils.hasText(safeKeyword)) {
            userCriteria = new Criteria().andOperator(
                    userCriteria,
                    Criteria.where("targetUser.nickname").regex(safeKeyword.trim(), "i"));
        }
        log.info("relations 用户侧过滤条件: {}", userCriteria.getCriteriaObject());

        int limit = (safeSize == null || safeSize <= 0) ? 20 : safeSize;

        // 3. follow 侧是 hex 字符串，user._id 是 ObjectId，需显式转换
        AggregationExpression toObjectId = ctx -> new Document("$convert",
                new Document("input", "$" + targetField)
                        .append("to", "objectId")
                        .append("onError", null));

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "created_at"),
                Aggregation.addFields()
                        .addField("targetOid")
                        .withValue(toObjectId)
                        .build(),
                Aggregation.lookup("user", "targetOid", "_id", "targetUser"),
                Aggregation.match(userCriteria),
                Aggregation.limit(limit),
                Aggregation.project()
                        .and("targetUser._id").as("_id")
                        .and("targetUser.nickname").as("nickname")
                        .and("targetUser.avatar").as("avatar")
                        .and("targetUser.profile").as("profile"));

        log.info("relations pipeline = {}", agg.toString());

        List<User> results = mongoTemplate.aggregate(agg, "follow", User.class).getMappedResults();
        log.info("relations 结果: size={} 首条=[id={}, nickname={}]",
                results.size(),
                results.isEmpty() ? null : results.get(0).getId(),
                results.isEmpty() ? null : results.get(0).getNickname());

        return results;
    }
}
