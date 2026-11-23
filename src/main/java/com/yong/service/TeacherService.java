package com.yong.service;

import java.util.List;
import java.util.Map;

import com.yong.pojo.Teacher;
import com.yong.pojo.TeacherQueryParam;
import com.yong.pojo.PageResult;

public interface TeacherService {

    // 分页查询
    PageResult<Teacher> page(TeacherQueryParam teacherQueryParam);

    // 新增教师
    void save(Teacher teacher) throws Exception;

    // 删除教师
    void delete(List<Integer> ids);

    //根据id查询教师信息
    Teacher getInfo(Integer id);

    //修改教师信息
    void update(Teacher teacher);

    List<Map<String, Object>> getTeacherIdOptions();

}
