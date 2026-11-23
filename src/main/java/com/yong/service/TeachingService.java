package com.yong.service;

import java.util.List;

import com.yong.pojo.Teaching;
import com.yong.pojo.TeachingQueryParam;
import com.yong.pojo.PageResult;

public interface TeachingService {

    // 分页查询
    PageResult<Teaching> page(TeachingQueryParam teachingQueryParam);

    // 新增授课安排
    void save(Teaching teaching) throws Exception;

    // 删除授课安排
    void delete(List<Integer> ids);

    //根据id查询授课安排信息
    Teaching getInfo(Integer id);

    //修改授课安排信息
    void update(Teaching teaching);

}
