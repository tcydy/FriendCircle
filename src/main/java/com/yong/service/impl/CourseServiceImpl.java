package com.yong.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yong.mapper.CourseMapper;
import com.yong.mapper.EnrollmentMapper;
import com.yong.mapper.TeachingMapper;
import com.yong.pojo.Course;
import com.yong.pojo.CourseQueryParam;
import com.yong.pojo.PageResult;
import com.yong.service.CourseService;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;

    private final TeachingMapper teachingMapper;

    private final EnrollmentMapper enrollmentMapper;

    CourseServiceImpl(CourseMapper courseMapper, TeachingMapper teachingMapper, EnrollmentMapper enrollmentMapper) {
        this.courseMapper = courseMapper;
        this.teachingMapper = teachingMapper;
        this.enrollmentMapper = enrollmentMapper;
    }

    // 分页查询
    @Override
    public PageResult<Course> page(CourseQueryParam courseQueryParam) {
        // 1.设置分页参数
        PageHelper.startPage(courseQueryParam.getPage(), courseQueryParam.getPageSize());
        // 2.执行查询
        List<Course> courseList = courseMapper.list(courseQueryParam);
        // 3.解析查询结果，并封装
        Page<Course> p = (Page<Course>) courseList;
        return new PageResult<Course>(p.getTotal(), p.getResult());
    }

    @Transactional(rollbackFor = { Exception.class })
    //删除对应id的课程数据
    @Override
    public void delete(List<Integer> ids) {
        courseMapper.delete(ids);//删除对应id的课程数据
        teachingMapper.deleteByCourseId(ids);//删除对应课程id的授课安排
        enrollmentMapper.deleteByCourseId(ids);//删除对应课程id的学生选课
    }

    //添加课程
    @Override
    public void add(Course course) {
        //1.补全基础属性：creatTime，updateTime
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        //2.调用Mapper方法
        courseMapper.add(course);
    }

    //根据id查询课程
    @Override
    public Course selectById(Integer id) {
        return courseMapper.selectById(id);
    }

    //修改课程
    @Override
    public void update(Course course) {
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.update(course);
    }

    //返回学生查询下拉列表
    @Override 
    public List<Map<String, Object>> getCourseIdOptions() {
        List<Map<String, Object>> list = courseMapper.getCourseIdOptions();
        return list;
    }

}
