package com.yong.pojo;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class TeacherQueryParam {

    private Integer page = 1;//页码
    private Integer pageSize = 10;//每页展示记录数
    private String name;//姓名
    private String title;//职称
    private Integer deptId;//院系ID
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin;//入职时间-开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;//入职时间-结束时间

    private Integer salaryMin;//薪资-最小值
    private Integer salaryMax;//薪资-最大值
}
