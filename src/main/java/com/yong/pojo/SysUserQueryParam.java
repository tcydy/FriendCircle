package com.yong.pojo;

import lombok.Data;

@Data
public class SysUserQueryParam {

    private Integer page = 1;//页码
    private Integer pageSize = 10;//每页展示记录数
    private String username;//姓名

}
