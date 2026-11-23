package com.yong.service.impl;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yong.mapper.EnrollmentMapper;

import com.yong.mapper.StudentMapper;
import com.yong.pojo.Student;

import com.yong.pojo.StudentQueryParam;

import com.yong.pojo.PageResult;

import com.yong.service.StudentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;

    private final EnrollmentMapper enrollmentMapper;


    StudentServiceImpl(StudentMapper studentMapper, EnrollmentMapper enrollmentMapper) {
        this.studentMapper = studentMapper;
        this.enrollmentMapper = enrollmentMapper;
    }


    // 分页查询
    @Override
    public PageResult<Student> page(StudentQueryParam studentQueryParam) {
        // 1.设置分页参数

        PageHelper.startPage(studentQueryParam.getPage(), studentQueryParam.getPageSize());
        // 2.执行查询
        List<Student> studentList = studentMapper.list(studentQueryParam);
        // 3.解析查询结果，并封装
        Page<Student> p = (Page<Student>) studentList;
        return new PageResult<Student>(p.getTotal(), p.getResult());
    }

    // 新增学生
    @Override
    public void save(Student student) throws Exception {
        // 保存学生基本信息
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        studentMapper.insert(student);
    }

    // 删除学生
    @Transactional(rollbackFor = { Exception.class })//出现异常都要回滚
    @Override
    public void delete(List<Integer> ids) {
        // 1.批量删除学生的基本信息
        studentMapper.delete(ids);
        // 2.批量删除学生的选课信息
        enrollmentMapper.batchDeleteByStudentId(ids);
    }

    //根据id查询学生信息
    @Override
    public Student getInfo(Integer id) {
        return studentMapper.getById(id);
    }

    //修改学生信息
    @Override
    public void update(Student student) {
        //根据id修改学生基本信息
        student.setUpdateTime(LocalDateTime.now());
        studentMapper.updateById(student);
    }

    //返回学生查询下拉列表
    @Override 
    public List<Map<String, Object>> getStudentIdOptions() {
        List<Map<String, Object>> list = studentMapper.getStudentIdOptions();
        return list;
    }

}
