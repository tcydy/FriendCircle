package com.yong.service.impl;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.yong.mapper.TeacherMapper;
import com.yong.mapper.TeachingMapper;
import com.yong.pojo.Teacher;

import com.yong.pojo.TeacherQueryParam;
import com.yong.pojo.PageResult;

import com.yong.service.TeacherService;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;

    private final TeachingMapper teachingMapper;


    TeacherServiceImpl(TeachingMapper teachingMapper, TeacherMapper teacherMapper) {
        this.teachingMapper = teachingMapper;
        this.teacherMapper = teacherMapper;
    }


    // 分页查询
    @Override
    public PageResult<Teacher> page(TeacherQueryParam teacherQueryParam) {
        // 1.设置分页参数
        PageHelper.startPage(teacherQueryParam.getPage(), teacherQueryParam.getPageSize());
        // 2.执行查询
        List<Teacher> teacherList = teacherMapper.list(teacherQueryParam);
        // 3.解析查询结果，并封装
        Page<Teacher> p = (Page<Teacher>) teacherList;
        return new PageResult<Teacher>(p.getTotal(), p.getResult());
    }

    // 新增教师
    @Override
    public void save(Teacher teacher) throws Exception {
        // 保存教师基本信息
        teacher.setCreateTime(LocalDateTime.now());
        teacher.setUpdateTime(LocalDateTime.now());
        teacherMapper.insert(teacher);
    }

    // 删除教师
    @Transactional(rollbackFor = { Exception.class })//出现异常都要回滚
    @Override
    public void delete(List<Integer> ids) {
        // 1.批量删除教师的基本信息
        teacherMapper.delete(ids);
        // 2.批量删除教师的授课信息
        teachingMapper.batchDeleteByTeacherId(ids);
    }

    //根据id查询教师信息
    @Override
    public Teacher getInfo(Integer id) {
        return teacherMapper.getById(id);
    }

    //修改教师信息
    @Override
    public void update(Teacher teacher) {
        //根据id修改教师基本信息
        teacher.setUpdateTime(LocalDateTime.now());
        teacherMapper.updateById(teacher);
    }

    //返回老师查询下拉列表
    @Override 
    public List<Map<String, Object>> getTeacherIdOptions() {
        List<Map<String, Object>> list = teacherMapper.getTeacherIdOptions();
        return list;
    }

}
