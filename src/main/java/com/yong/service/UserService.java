package com.yong.service;

import java.util.List;

import com.yong.pojo.User;
import com.yong.pojo.PageResult;

public interface UserService {

    // 新增用户
    void save(User sysuser) throws Exception;

    //修改用户信息
    void update(User sysuser);

    //登录
    User login(User user);

    //判断用户名是否重复
    boolean isUsernameExist(User user);

    //修改用户密码
    boolean updatePassword(User user);


    // 修改用户信息
    User getInfo(String id);

    void updateProfileById(User user);

    void updateLocationById(String id, Double longitude, Double latitude);

	void follow(String userId, String targetUserId, Integer action);

}
