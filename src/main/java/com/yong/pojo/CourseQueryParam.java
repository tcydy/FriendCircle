package com.yong.pojo;

import lombok.Data;

@Data
public class CourseQueryParam {

    private Integer page = 1;//页码
    private Integer pageSize = 10;//每页展示记录数
    private String name;//课程名称

    private Double creditsMin;//学分-最小值
    private Double creditsMax;//学分-最大值
}
