package com.yong.pojo;

import lombok.Data;

@Data
public class DeptQueryParam {
    private Integer page = 1;//页码
    private Integer pageSize = 10;//每页展示记录数
    private String name;//课程名称
    private String officeLocation;//办公地点
}
