package com.yong.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.springframework.stereotype.Service;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;


import com.yong.mapper.SysUserMapper;
import com.yong.pojo.SysUser;

import com.yong.pojo.SysUserQueryParam;
import com.yong.pojo.PageResult;

import com.yong.service.SysUserService;
import com.yong.utils.JwtUtils;


import cn.hutool.crypto.digest.BCrypt;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysuserMapper;

    SysUserServiceImpl(SysUserMapper sysuserMapper) {
        this.sysuserMapper = sysuserMapper;
    }

    // 分页查询
    @Override
    public PageResult<SysUser> page(SysUserQueryParam sysuserQueryParam) {
        // 1.设置分页参数

        PageHelper.startPage(sysuserQueryParam.getPage(), sysuserQueryParam.getPageSize());
        // 2.执行查询
        List<SysUser> sysuserList = sysuserMapper.list(sysuserQueryParam);
        // 3.解析查询结果，并封装
        Page<SysUser> p = (Page<SysUser>) sysuserList;
        return new PageResult<SysUser>(p.getTotal(), p.getResult());
    }

    // 新增用户
    @Override
    public void save(SysUser sysuser) throws Exception {
        // 加密：gensalt() 生成随机盐，hashpw 返回 60 位密文
        String rawPassword = sysuser.getPassword();
        String encrypted = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        sysuser.setPassword(encrypted);
        sysuserMapper.insert(sysuser);// 保存用户基本信息
    }

    // 判断用户名是否重复
    @Override
    public boolean isUsernameExist(SysUser sysUser) {
        SysUser sysuser = sysuserMapper.getByUsername(sysUser.getUsername());
        if (sysuser == null)
            return false;
        return true;
    }

    // 删除用户
    @Override
    public void delete(List<Integer> ids) {
        // 批量删除用户的基本信息
        sysuserMapper.delete(ids);
    }

    // 根据id查询用户信息
    @Override
    public SysUser getInfo(Integer id) {
        return sysuserMapper.getById(id);
    }

    // 修改用户信息
    @Override
    public void update(SysUser sysuser) {
        // 根据id修改用户基本信息
        sysuserMapper.updateById(sysuser);
    }

    // 登录
    @Override
    public SysUser login(SysUser sysUser) {
        // 1.通过用户名查询用户，判断用户名是否存在
        SysUser s = sysuserMapper.getByUsername(sysUser.getUsername());
        // 2.如果成功，组装登录成功信息
        if (s != null) {
            // 2.校验密码：明文 vs 数据库密文
            boolean match = BCrypt.checkpw(sysUser.getPassword(), s.getPassword());
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

    // 修改用户密码 成功：true，失败：false
    @Override
    public boolean updatePassword(SysUser sysUser) {
        // 只修改Token里的用户
        Map<String, Object> claims = JwtUtils.parseToken(sysUser.getToken());
        SysUser sysUser2 = sysuserMapper.getByUsername((String) claims.get("username"));
        // token的信息对不上
        if (sysUser2.getId() != (Integer) claims.get("id") || (Integer) claims.get("id") != sysUser.getId())
            return false;

        // 校验密码：明文 vs 数据库密文
        boolean match = BCrypt.checkpw(sysUser.getPassword(), sysUser2.getPassword());
        if (!match)
            return false;

        //更新密码
        String rawPassword = sysUser.getNewPassword();// 加密：gensalt() 生成随机盐，hashpw 返回 60 位密文
        String encrypted = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        sysUser.setPassword(encrypted);
        sysuserMapper.updateById(sysUser);
        return true;
    }
    
    // 修改用户密码 成功：true，失败：false
    @Override
    public void resetPassword(Integer id) {
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        String rawPassword = "123456";// 加密：gensalt() 生成随机盐，hashpw 返回 60 位密文
        String encrypted = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        sysUser.setPassword(encrypted);
        sysuserMapper.updateById(sysUser);
    }
}
