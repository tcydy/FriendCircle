package com.yong.repository.custom.impl;

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

    //根据id更新密码
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

    //根据id更新位置
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


}
