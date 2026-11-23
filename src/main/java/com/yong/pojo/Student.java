package com.yong.pojo;

import java.time.LocalDateTime;
import java.time.Year;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//员工
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Integer id; //ID,主键
    private String name; //姓名
    private Integer gender; //性别, F:女, M:男
    private String image; //头像
    private Year enrollmentYear; //入学年份
    private Integer deptId; //关联的院系ID
    private Integer classId; //关联的班级ID
    private LocalDateTime createTime; //创建时间
    private LocalDateTime updateTime; //修改时间
    
    private String deptName;//封装院系名称
    private String className;//封装班级名称
}
