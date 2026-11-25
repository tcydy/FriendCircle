package com.yong.repository.custom.impl;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.yong.pojo.Follow;
import com.yong.pojo.User;
import com.yong.repository.custom.FollowCustomRepository;

@Repository
public class FollowRepositoryImpl implements FollowCustomRepository {

    private final MongoTemplate mongoTemplate;

    FollowRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Follow findByFollowerIdAndFolloweeId(String followerId, String followeeId) {
        Query query = Query.query(
                Criteria.where("followerId").is(followerId)
                        .and("followeeId").is(followeeId));

        // 执行查询：查找匹配的第一条记录
        return mongoTemplate.findOne(query, Follow.class);
    }

    @Override
    public void saveNoReturn(Follow follow) {
        mongoTemplate.save(follow);
    }

    @Override
    public void delete(Follow follow) {
        mongoTemplate.remove(follow);
    }

    @Override
    public void updateProfileById(User user) {
        // 关注者是用户
        // 条件
        Query query1 = Query.query(Criteria.where("followerId").is(user.getId()));
        // 要更新的字段
        Update update1 = new Update()
                .set("followerNickname", user.getNickname())
                .set("followerAvatar", user.getAvatar());
        // 执行更新：更新第一条匹配的记录
        mongoTemplate.updateMulti(query1, update1, Follow.class);

        // 被关注者是用户
        // 条件
        Query query2 = Query.query(Criteria.where("followeeId").is(user.getId()));
        // 要更新的字段
        Update update2 = new Update()
                .set("followeeNickname", user.getNickname())
                .set("followeeAvatar", user.getAvatar());
        // 执行更新：更新第一条匹配的记录
        mongoTemplate.updateMulti(query2, update2, Follow.class);
    }

}
