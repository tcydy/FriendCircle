package com.yong.pojo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clss {
    private Integer id;//ID,主键
    private String name;//班级名称
    private Integer deptId;//院系ID
    private LocalDateTime createTime; //创建时间
    private LocalDateTime updateTime; //修改时间

    private String deptName;//封装院系名称
}