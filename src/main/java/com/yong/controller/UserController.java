package com.yong.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yong.pojo.Result;
import com.yong.pojo.User;
import com.yong.service.UserService;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PutMapping;



@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    // 登录
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        log.info("登录：{}", user);
        User newUser = userService.login(user);
        if (newUser != null) {
            return Result.success(newUser);
        }
        return Result.error("用户名或密码错误！");
    }

    // 注册
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        log.info("注册：{}", user);
        // 判断用户名、密码是否为空
        if (StrUtil.isEmpty(user.getUsername()) || StrUtil.isEmpty(user.getPassword())) {
            return Result.error("用户名或密码不能为空");
        }
        // 判断用户名是否存在
        if (!userService.isUsernameExist(user.getUsername())) {
            try {
                userService.save(user);
                return Result.success();
            } catch (Exception e) {
                e.printStackTrace();
                return Result.error("注册出错。");
            }
        }
        return Result.error("用户名已存在。");
    }

    // 修改密码
    @PostMapping("/password")
    public Result updatePassword(@RequestBody User user) {
        if (StrUtil.isEmpty(user.getPassword()) || StrUtil.isBlank(user.getNewPassword())) {
            return Result.error("旧密码不正确或输入为空");
        }
        if (!userService.updatePassword(user)) {
            return Result.error("旧密码不正确或输入为空");
        }
        return Result.success();
    }

    //检查token
    @GetMapping("/checkToken")
    public Result checkToken() {
        return Result.success();
    }

    //获取用户信息
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable String id) {
        log.info("根据id查询用户信息：{}", id);
        User user = userService.getInfo(id);
        return Result.success(user);
    }

    //编辑资料
    @PutMapping("/profile")
    public Result update(@RequestBody User user) {
        log.info("根据id修改用户信息：{}", user);
        userService.updateProfileById(user);
        return Result.success();
    }

    //更新位置（传入用户id，经度、纬度）
    @PutMapping("/location")
    public Result updateLocationById(@PathVariable String id, @RequestBody Double longitude,
            @RequestBody Double latitude) {
        userService.updateLocationById(id, longitude, latitude);
        return Result.success();
    }
    
    //关注/取关
    @PostMapping("/follow/{targetUserId}")
    public Result follow(@RequestBody Integer action,@RequestHeader String token,@PathVariable String targetUserId) {
        userService.selectFollow(token, targetUserId, action);
        return Result.success();
    }
    


}
