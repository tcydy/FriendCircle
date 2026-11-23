package com.yong.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yong.mapper.ClssMapper;
import com.yong.mapper.StudentMapper;
import com.yong.pojo.Clss;
import com.yong.pojo.ClssQueryParam;
import com.yong.pojo.PageResult;
import com.yong.service.ClssService;

@Service
public class ClssServiceImpl implements ClssService {

    private final ClssMapper clssMapper;

    private final StudentMapper studentMapper;

    ClssServiceImpl(ClssMapper clssMapper, StudentMapper studentMapper) {
        this.clssMapper = clssMapper;
        this.studentMapper = studentMapper;
    }

    // 分页查询
    @Override
    public PageResult<Clss> page(ClssQueryParam clssQueryParam) {
        // 1.设置分页参数
        PageHelper.startPage(clssQueryParam.getPage(), clssQueryParam.getPageSize());
        // 2.执行查询
        List<Clss> clssList = clssMapper.list(clssQueryParam);
        // 3.解析查询结果，并封装
        Page<Clss> p = (Page<Clss>) clssList;
        return new PageResult<Clss>(p.getTotal(), p.getResult());
    }

    @Transactional(rollbackFor = { Exception.class })
    //删除对应id的班级数据
    @Override
    public void delete(List<Integer> ids) {
        clssMapper.delete(ids);//删除对应id的班级数据
        studentMapper.updateByClassId(ids);//把学生的班级ID置为null
    }

    //添加班级
    @Override
    public void add(Clss clss) {
        //1.补全基础属性：creatTime，updateTime
        clss.setCreateTime(LocalDateTime.now());
        clss.setUpdateTime(LocalDateTime.now());
        //2.调用Mapper方法
        clssMapper.add(clss);
    }

    //根据id查询班级
    @Override
    public Clss selectById(Integer id) {
        return clssMapper.selectById(id);
    }

    //修改班级
    @Override
    public void update(Clss clss) {
        clss.setUpdateTime(LocalDateTime.now());
        clssMapper.update(clss);
    }

    //返回班级查询下拉列表
    @Override 
    public List<Map<String, Object>> getClassIdOptions() {
        List<Map<String, Object>> list = clssMapper.getClassIdOptions();
        return list;
    }

}
