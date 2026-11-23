package com.yong.pojo;

import lombok.Data;

@Data
public class EnrollmentQueryParam {

    private Integer page = 1;//页码
    private Integer pageSize = 10;//每页展示记录数
    private Integer studentId;//学生ID
    private Integer courseId;//课程ID
    private String semester;//学期

    private double gradeMin;//成绩最小值
    private double gradeMax;//成绩最大值
}
