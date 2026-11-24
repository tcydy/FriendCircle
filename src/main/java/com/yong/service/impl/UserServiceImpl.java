package com.yong.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.yong.pojo.User;
import com.yong.pojo.Follow;

import com.yong.repository.UserRepository;
import com.yong.pojo.PageResult;

import com.yong.service.UserService;
import com.yong.utils.JwtUtils;


import cn.hutool.crypto.digest.BCrypt;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    UserServiceImpl(UserRepository userRepository,FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }


    // 登录
    @Override
    public User login(User user) {
        // 1.通过用户名查询用户，判断用户名是否存在
        List<User> list = userRepository.findByUsername(user.getUsername());

        // 2.如果成功，组装登录成功信息
        if (list != null && !list.isEmpty()) {
            User s = list.get(0);
            // 2.校验密码：明文 vs 数据库密文
            boolean match = BCrypt.checkpw(user.getPassword(), s.getPassword());
            if (match) {
                // 生成JWT令牌
                Map<String, Object> claims = new HashMap<>();
                claims.put("id", s.getId());
                claims.put("username", s.getUsername());
                String jwt = JwtUtils.generateToken(claims);
                s.setToken(jwt);
                s.setPassword("");// 设置返回的密码为空，保护密码不泄露
                log.info("登录成功，用户信息：{}", s);
                return s;
            }
        }
        // 3.如果失败，返回空
        return null;
    }
    
    // 新增用户
    @Override
    public void save(User user) throws Exception {
        // 加密：gensalt() 生成随机盐，hashpw 返回 60 位密文
        String rawPassword = user.getPassword();
        String encrypted = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        user.setPassword(encrypted);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);// 保存用户基本信息
    }

    // 修改用户密码 成功：true，失败：false
    @Override
    public boolean updatePassword(User user) {
        // 只修改Token里的用户
        Map<String, Object> claims = JwtUtils.parseToken(user.getToken());
        List<User> list = userRepository.findByUsername((String) claims.get("username"));
        //用户名不存在
        if (list == null || list.isEmpty()) {
            return false;
        }

        User user2 = list.get(0);
        // token的信息对不上
        if (!Objects.equals(user2.getId(),claims.get("id")) || !Objects.equals(claims.get("id"),user.getId()))
            return false;

        // 校验密码：明文 vs 数据库密文
        boolean match = BCrypt.checkpw(user.getPassword(), user2.getPassword());
        if (!match)
            return false;

        //更新密码
        String rawPassword = user.getNewPassword();// 加密：gensalt() 生成随机盐，hashpw 返回 60 位密文
        String encrypted = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        user.setPassword(encrypted);
        userRepository.updatePasswordById(user);
        return true;
    }

    // 根据id查询用户信息
    @Override
    public User getInfo(String id) {
        User user = userRepository.findById(id).orElse(null);
        return user;
    }

    // 修改用户信息
    @Override
    public void updateProfileById(User user) {
        userRepository.updateProfileById(user);
    }

    // 根据id修改经纬度
    @Override
    public void updateLocationById(String id, Double longitude, Double latitude){
        userRepository.updateLocationById(id,longitude,latitude);
    }

    //关注/取关
    @Override
    public void follow(String userId, String targetUserId, Integer action) {
        //关注
        if (action == 0) {
            User from = userRepository.findById(userId).orElse(null);
            User to = userRepository.findById(targetUserId).orElse(null);
            Follow follow = new Follow()
                    .setFollowerId(userId)
                    .setFolloweeId(targetUserId)
                    .setFollowerNickName(from.getNickname())
                    .setFolloweeNickName(to.getNickname())
                    .setFollowerAvatar(from.getAvatar())
                    .setFolloweeAvatar(to.getAvatar())
                    .setCreatedAt(LocalDateTime.now());
            followRepository.save(follow);
        }
        //取关
        if (action == 1) {
            followRepository.deleteByFollowerIdAndFolloweeId(userId,targetUserId);
        }
    }

}
