package com.yong.repository;



import org.springframework.data.mongodb.repository.MongoRepository;

import com.yong.pojo.Follow;

import com.yong.repository.custom.FollowCustomRepository;

//用户
public interface FollowRepository extends MongoRepository<Follow, String>, FollowCustomRepository {

    public Follow findByFollowerIdAndFolloweeId(String followerId,String followeeId);

    
}
