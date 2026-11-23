package com.yong.service;

import java.util.List;
import java.util.Map;

import com.yong.pojo.Student;
import com.yong.pojo.StudentQueryParam;
import com.yong.pojo.PageResult;

public interface StudentService {

    // 分页查询
    PageResult<Student> page(StudentQueryParam studentQueryParam);

    // 新增学生
    void save(Student student) throws Exception;

    // 删除学生
    void delete(List<Integer> ids);

    //根据id查询学生信息
    Student getInfo(Integer id);

    //修改学生信息
    void update(Student student);

    //返回学生查询下拉列表
    List<Map<String, Object>> getStudentIdOptions();

}
