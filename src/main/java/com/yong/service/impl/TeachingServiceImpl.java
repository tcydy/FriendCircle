package com.yong.service.impl;

import java.util.List;



import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.yong.mapper.TeachingMapper;
import com.yong.pojo.Teaching;

import com.yong.pojo.TeachingQueryParam;

import com.yong.pojo.PageResult;

import com.yong.service.TeachingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class TeachingServiceImpl implements TeachingService {

    private final TeachingMapper teachingMapper;

    TeachingServiceImpl(TeachingMapper teachingMapper) {
        this.teachingMapper = teachingMapper;
    }

    // 分页查询
    @Override
    public PageResult<Teaching> page(TeachingQueryParam teachingQueryParam) {
        // 1.设置分页参数

        PageHelper.startPage(teachingQueryParam.getPage(), teachingQueryParam.getPageSize());
        // 2.执行查询
        List<Teaching> teachingList = teachingMapper.list(teachingQueryParam);
        // 3.解析查询结果，并封装
        Page<Teaching> p = (Page<Teaching>) teachingList;
        return new PageResult<Teaching>(p.getTotal(), p.getResult());
    }

    // 新增授课安排
    @Override
    public void save(Teaching teaching) throws Exception {
        // 保存授课安排基本信息
        teachingMapper.insert(teaching);
    }

    // 删除授课安排
    @Override
    public void delete(List<Integer> ids) {
        // 批量删除授课安排的基本信息
        teachingMapper.delete(ids);
    }

    //根据id查询授课安排信息
    @Override
    public Teaching getInfo(Integer id) {
        return teachingMapper.getById(id);
    }

    //修改授课安排信息
    @Override
    public void update(Teaching teaching) {
        //根据id修改授课安排基本信息
        teachingMapper.updateById(teaching);
    }

}
