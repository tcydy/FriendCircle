package com.yong.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.yong.pojo.SysUser;
import com.yong.pojo.SysUserQueryParam;

//用户信息
@Mapper
public interface SysUserMapper {
    //查询
    public List<SysUser> list(SysUserQueryParam sysuserQueryParam);

    //新增用户基本信息
    @Options(useGeneratedKeys=true,keyProperty="id")//获取到生成的主键
    @Insert("insert into sys_user (username,password,image) values (#{username},#{password},#{image});")
    public void insert(SysUser sysuser);

    //删除用户
    public void delete(List<Integer> ids);

    //根据id查询用户信息
    @Select("select id, username, password, image from sys_user where id=#{id}")
    public SysUser getById(Integer id);

    //根据id修改用户基本信息
    public void updateById(SysUser sysuser);

    //根据用户名查找用户
    @Select("select id, username, password, image from sys_user where username=#{username}")
    public SysUser getByUsername(String username);

}
