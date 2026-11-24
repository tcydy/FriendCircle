package com.yong.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yong.pojo.User;
import com.yong.repository.custom.UserCustomRepository;

//用户
public interface UserRepository extends MongoRepository<User, String>, UserCustomRepository {

    List<User> findByUsername(String username);

    void updateProfileById(User user);

    void updatePasswordById(User user);

    void updateLocationById(String id, Double longitude, Double latitude);



    
}
