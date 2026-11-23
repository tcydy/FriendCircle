package com.yong.pojo;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {
    private Integer id;//ID,主键
    private String username;//用户名称
    private String password;//密码
    private String image;//头像URL

    @TableField(exist=false)
    private String newPassword;
    
    @TableField(exist=false)
    private  String token;
}