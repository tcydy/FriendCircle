package com.yong.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yong.pojo.SysUser;
import com.yong.pojo.SysUserQueryParam;
import com.yong.pojo.PageResult;
import com.yong.pojo.Result;
import com.yong.service.SysUserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

//用户管理
@Slf4j
@RequestMapping("/sysusers")
@RestController
public class SysUserController {

    private final SysUserService sysuserService;

    SysUserController(SysUserService sysuserService) {
        this.sysuserService = sysuserService;
    }

    // 分页查询
    @GetMapping
    public Result page(SysUserQueryParam sysuserQueryParam) {
        log.info("分页查询：{}", sysuserQueryParam);
        PageResult<SysUser> pageResult = sysuserService.page(sysuserQueryParam);
        return Result.success(pageResult);
    }

    // 新增用户
    @PostMapping
    public Result save(@RequestBody SysUser sysuser) throws Exception {
        log.info("添加用户：{}", sysuser);
        sysuserService.save(sysuser);
        return Result.success();
    }

    // 删除用户
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("删除用户：{}", ids);
        sysuserService.delete(ids);
        return Result.success();
    }

    //根据id查询用户信息
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("根据id查询用户信息：{}", id);
        SysUser sysuser = sysuserService.getInfo(id);
        return Result.success(sysuser);
    }
    
    //修改用户信息
    @PutMapping
    public Result update(@RequestBody SysUser sysuser) {
        log.info("修改用户：{}", sysuser);
        sysuserService.update(sysuser);
        return Result.success();
    }

    //重置密码（123456）
    @GetMapping("/resetPassword/{id}")
    public Result resetPassword(@PathVariable Integer id) {
        sysuserService.resetPassword(id);
        return Result.success();
    }

}
