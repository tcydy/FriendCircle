package com.yong.service.impl;

import java.util.List;


import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.yong.mapper.EnrollmentMapper;
import com.yong.pojo.Enrollment;

import com.yong.pojo.EnrollmentQueryParam;

import com.yong.pojo.PageResult;

import com.yong.service.EnrollmentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;

    EnrollmentServiceImpl(EnrollmentMapper enrollmentMapper) {
        this.enrollmentMapper = enrollmentMapper;
    }

    // 分页查询
    @Override
    public PageResult<Enrollment> page(EnrollmentQueryParam enrollmentQueryParam) {
        // 1.设置分页参数
        PageHelper.startPage(enrollmentQueryParam.getPage(), enrollmentQueryParam.getPageSize());
        // 2.执行查询
        List<Enrollment> enrollmentList = enrollmentMapper.list(enrollmentQueryParam);
        // 3.解析查询结果，并封装
        Page<Enrollment> p = (Page<Enrollment>) enrollmentList;
        return new PageResult<Enrollment>(p.getTotal(), p.getResult());
    }

    // 新增学生选课
    @Override
    public void save(Enrollment enrollment) throws Exception {
        // 保存学生选课基本信息
        enrollmentMapper.insert(enrollment);
    }

    // 删除学生选课
    @Override
    public void delete(List<Integer> ids) {
        // 批量删除学生选课的基本信息
        enrollmentMapper.delete(ids);
    }

    //根据id查询学生选课信息
    @Override
    public Enrollment getInfo(Integer id) {
        return enrollmentMapper.getById(id);
    }

    //修改学生选课信息
    @Override
    public void update(Enrollment enrollment) {
        //根据id修改学生选课基本信息
        enrollmentMapper.updateById(enrollment);
    }

}
