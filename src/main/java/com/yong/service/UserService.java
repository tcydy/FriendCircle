package com.yong.service;

import java.time.LocalDateTime;
import java.util.List;

import com.yong.pojo.User;

public interface UserService {

    // 登录
    User login(User user);

    // 新增用户
    void save(User sysuser) throws Exception;

    // 修改用户密码
    boolean updatePassword(User user, String token);

    // 修改用户信息
    User getInfo(String id);

    void updateProfileById(User user);

    void updateLocationById(String token, Double longitude, Double latitude);

    //判断用户名是否重复
    boolean isUsernameExist(String username);

    Boolean selectFollow(String token, String targetUserId, Integer action);

    // 获取关注/被关注列表
    List<User> relations(String keyword, Integer type, Integer size, LocalDateTime lastCreatedAt, String token);

}
