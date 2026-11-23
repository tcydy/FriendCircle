package com.yong.service;

import java.util.List;
import java.util.Map;

import com.yong.pojo.Dept;
import com.yong.pojo.DeptQueryParam;
import com.yong.pojo.PageResult;

public interface DeptService {

    // 分页查询
    PageResult<Dept> page(DeptQueryParam deptQueryParam);

    //添加院系
    void add(Dept dept);

    //根据id查询院系
    Dept selectById(Integer id);

    //修改院系
    void update(Dept dept);

    List<Map<String, Object>> getDeptIdOptions();

    void delete(List<Integer> ids);

}
