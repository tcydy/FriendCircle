package com.yong.pojo;

import java.time.Year;

import lombok.Data;

@Data
public class StudentQueryParam {

    private Integer page = 1;//页码
    private Integer pageSize = 10;//每页展示记录数
    private String name;//姓名
    private Integer gender;//性别
    private Integer deptId;//院系ID
    private Integer classId;//班级ID

    private Year begin;//入学时间-开始时间
    private Year end;//入学时间-结束时间
}
