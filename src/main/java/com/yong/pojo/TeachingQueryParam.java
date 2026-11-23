package com.yong.pojo;

import lombok.Data;

@Data
public class TeachingQueryParam {

    private Integer page = 1;//页码
    private Integer pageSize = 10;//每页展示记录数
    private Integer teacherId;//老师ID
    private Integer courseId;//课程ID
    private String semester;//学期
    
}
