package com.yong.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yong.mapper.DeptMapper;
import com.yong.mapper.StudentMapper;
import com.yong.mapper.TeacherMapper;
import com.yong.pojo.Dept;
import com.yong.pojo.DeptQueryParam;
import com.yong.pojo.PageResult;
import com.yong.service.DeptService;

@Service
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;

    private final TeacherMapper teacherMapper;

    private final StudentMapper studentMapper;

    DeptServiceImpl(DeptMapper deptMapper, TeacherMapper teacherMapper, StudentMapper studentMapper) {
        this.deptMapper = deptMapper;
        this.teacherMapper = teacherMapper;
        this.studentMapper = studentMapper;
    }

    // 分页查询
    @Override
    public PageResult<Dept> page(DeptQueryParam deptQueryParam) {
        // 1.设置分页参数
        PageHelper.startPage(deptQueryParam.getPage(), deptQueryParam.getPageSize());
        // 2.执行查询
        List<Dept> deptList = deptMapper.list(deptQueryParam);
        // 3.解析查询结果，并封装
        Page<Dept> p = (Page<Dept>) deptList;
        return new PageResult<Dept>(p.getTotal(), p.getResult());
    }

    @Transactional(rollbackFor = { Exception.class })
    //删除对应id的院系数据
    @Override
    public void delete(List<Integer> ids) {
        deptMapper.delete(ids);
        teacherMapper.updateByDeptId(ids);//删除教师对应的院系id
        studentMapper.updateByDeptId(ids);//删除学生对应的院系id
    }

    //添加院系
    @Override
    public void add(Dept dept) {
        //1.补全基础属性：creatTime，updateTime
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        //2.调用Mapper方法
        deptMapper.add(dept);
    }

    //根据id查询院系
    @Override
    public Dept selectById(Integer id) {
        return deptMapper.selectById(id);
    }

    //修改院系
    @Override
    public void update(Dept dept) {
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.update(dept);
    }

    //返回院系查询下拉列表
    @Override 
    public List<Map<String, Object>> getDeptIdOptions() {
        List<Map<String, Object>> list = deptMapper.getDeptIdOptions();
        return list;
    }
}
