package com.yong.service;

import java.util.List;

import com.yong.pojo.SysUser;
import com.yong.pojo.SysUserQueryParam;
import com.yong.pojo.PageResult;

public interface SysUserService {

    // 分页查询
    PageResult<SysUser> page(SysUserQueryParam sysuserQueryParam);

    // 新增用户
    void save(SysUser sysuser) throws Exception;

    // 删除用户
    void delete(List<Integer> ids);

    //根据id查询用户信息
    SysUser getInfo(Integer id);

    //修改用户信息
    void update(SysUser sysuser);

    //登录
    SysUser login(SysUser sysUser);

    //判断用户名是否重复
    boolean isUsernameExist(SysUser sysUser);

    //修改用户密码
    boolean updatePassword(SysUser sysUser);

    void resetPassword(Integer id);

}
