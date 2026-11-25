package com.yong.repository.custom.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.yong.pojo.User;
import com.yong.repository.custom.UserCustomRepository;

@Repository
public class UserRepositoryImpl implements UserCustomRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    // 根据id更新用户信息
    @Override
    public void updateProfileById(User user) {
        // 条件
        Query query = Query.query(Criteria.where("id").is(user.getId()));

        // 要更新的字段
        Update update = new Update()
                .set("nickname", user.getNickname())
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

}
