package com.yong.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yong.pojo.User;
import com.yong.repository.custom.UserCustomRepository;

//用户
public interface UserRepository extends MongoRepository<User, String>, UserCustomRepository {

    public List<User> findByUsername(String username);

    public void updateProfileById(User user);

    public void updatePasswordById(User user);

    public void updateLocationById(String id, Double longitude, Double latitude);

    public boolean isUsernameExist(String username);

    public void updateStats(String userId, int followingDelta, int followerDelta);

}
