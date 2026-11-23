package com.yong.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//授课安排
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teaching {
    private Integer id;//主键
    private Integer teacherId; //教授老师ID
    private Integer courseId; //课程ID
    private String semester;//学期，如：学期(如 2023-2024-1)

    private String teacherName;//封装老师名字
    private String courseName;//封装课程名称
}