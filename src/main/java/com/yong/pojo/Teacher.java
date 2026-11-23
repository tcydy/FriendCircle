package com.yong.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//教师
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    private Integer id; //ID,主键
    private String name; //姓名
    private LocalDate entryDate; //入职日期
    private Integer deptId; //关联的院系ID
    private String title;//职称
    private Integer salary; //薪资
    private String image; //头像
    private LocalDateTime createTime; //创建时间
    private LocalDateTime updateTime; //修改时间
    
    private String deptName;//封装院系名称
}
