package com.yong.service;

import com.yong.pojo.User;

public interface UserService {

    // 登录
    User login(User user);

    // 新增用户
    void save(User sysuser) throws Exception;

    // 修改用户密码
    boolean updatePassword(User user);

    // 修改用户信息
    User getInfo(String id);

    void updateProfileById(User user);

    void updateLocationById(String id, Double longitude, Double latitude);

    //判断用户名是否重复
    boolean isUsernameExist(String username);

    Boolean selectFollow(String token, String targetUserId, Integer action);

}
