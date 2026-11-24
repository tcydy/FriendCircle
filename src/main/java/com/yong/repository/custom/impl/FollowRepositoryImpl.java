package com.yong.repository.custom.impl;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.yong.pojo.Follow;
import com.yong.repository.custom.FollowCustomRepository;

@Repository
public class FollowRepositoryImpl implements FollowCustomRepository {

    private MongoTemplate mongoTemplate;

    public Follow findByFollowerIdAndFolloweeId(String followerId,String followeeId) {
        Query query = Query.query(
                Criteria.where("followerId").is(followerId)
                        .and("followeeId").is(followeeId)
        );

        // 执行查询：查找匹配的第一条记录
        return mongoTemplate.findOne(query, Follow.class);
    }



}
