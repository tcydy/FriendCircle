package com.yong.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//学生选课
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    private Integer id;//主键
    private Integer studentId; //选课学生ID
    private Integer courseId; //课程ID
    private String semester;//学期，如：学期(如 2023-2024-1)
    private Double grade;//成绩

    private String studentName;//封装学生名字
    private String courseName;//封装课程名称
}