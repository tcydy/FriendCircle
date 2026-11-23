package com.yong.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yong.pojo.Result;
import com.yong.service.SysUserService;
import com.yong.utils.AliyunOSSOperator;


import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;



@Slf4j
@RequestMapping("/web")
@RestController
public class WebController {

    // 本地磁盘存储的访问路径
    /*
     * @PostMapping("/upload")
     * public Result upload(String name,Integer age,MultipartFile file) throws
     * IllegalStateException, IOException {
     * log.info("接受参数：{},{},{}", name, age, file);
     * //获得原始文件名
     * String originalFilename = file.getOriginalFilename();
     * 
     * //新的文件名
     * String extension =
     * originalFilename.substring(originalFilename.lastIndexOf("."));
     * String newFileName = UUID.randomUUID().toString() + extension;
     * 
     * //保存文件
     * file.transferTo(new File("D:/images/"+newFileName));
     * return Result.success();
     * }
     */

    private final AliyunOSSOperator aliyunOSSOperator;

    private final SysUserService sysUserService;

    WebController(SysUserService sysUserService, AliyunOSSOperator aliyunOSSOperator) {
        this.sysUserService = sysUserService;
        this.aliyunOSSOperator = aliyunOSSOperator;
    }

    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws Exception {
        log.info("文件上传：{}", file.getOriginalFilename());
        // 将文件交给OSS存储管理
        String url = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename());
        log.info("文件上传OSS，url：{}", url);

        return Result.success(url);
    }

    // 登录
    @PostMapping("/login")
    public Result login(@RequestBody SysUser sysUser) {
        log.info("登录：{}", sysUser);
        SysUser newSysUser = sysUserService.login(sysUser);
        if (newSysUser != null) {
            return Result.success(newSysUser);
        }
        return Result.error("用户名或密码错误！");
    }

    // 注册
    @PostMapping("/register")
    public Result postMethodName(@RequestBody SysUser sysUser) {
        log.info("注册：{}", sysUser);
        // 判断用户名、密码是否为空
        if (StrUtil.isEmpty(sysUser.getUsername()) || StrUtil.isEmpty(sysUser.getPassword())) {
            return Result.error("用户名或密码不能为空");
        }
        // 判断用户名是否存在
        if (!sysUserService.isUsernameExist(sysUser)) {
            try {
                sysUserService.save(sysUser);
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
    public Result updatePassword(@RequestBody SysUser sysUser) {
        if (StrUtil.isEmpty(sysUser.getPassword()) || StrUtil.isBlank(sysUser.getNewPassword())) {
            return Result.error("旧密码不正确或输入为空");
        }
        if (!sysUserService.updatePassword(sysUser)) {
            return Result.error("旧密码不正确或输入为空");
        }
        return Result.success();
    }

    //检查token
    @GetMapping("/checkToken")
    public Result checkToken() {
        return Result.success();
    }
    
}
