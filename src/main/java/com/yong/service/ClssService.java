package com.yong.service;


import java.util.List;
import java.util.Map;

import com.yong.pojo.Clss;
import com.yong.pojo.ClssQueryParam;
import com.yong.pojo.PageResult;

public interface ClssService {

    // 分页查询
    PageResult<Clss> page(ClssQueryParam clssQueryParam);  

    //添加班级
    void add(Clss clss);

    //根据id查询班级
    Clss selectById(Integer id);

    //修改班级
    void update(Clss clss);

    List<Map<String, Object>> getClassIdOptions();

    void delete(List<Integer> ids);

}
