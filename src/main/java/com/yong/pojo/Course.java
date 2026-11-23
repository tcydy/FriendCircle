package com.yong.pojo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//员工
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Integer id; //ID,主键
    private String name; //课程名称
    private Double credits; //学分

    private LocalDateTime createTime; //创建时间
    private LocalDateTime updateTime; //修改时间
}
