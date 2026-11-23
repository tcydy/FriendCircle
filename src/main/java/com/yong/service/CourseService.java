package com.yong.service;

import java.util.List;
import java.util.Map;

import com.yong.pojo.Course;
import com.yong.pojo.CourseQueryParam;
import com.yong.pojo.PageResult;

public interface CourseService {

    // 分页查询
    PageResult<Course> page(CourseQueryParam courseQueryParam);  

    //删除对应id的课程数据
    void delete(List<Integer> ids);

    //添加课程
    void add(Course course);

    //根据id查询课程
    Course selectById(Integer id);

    //修改课程
    void update(Course course);

    //返回学生查询下拉列表
    List<Map<String, Object>> getCourseIdOptions();

}
