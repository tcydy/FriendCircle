package com.yong.service;

import java.util.List;

import com.yong.pojo.Enrollment;
import com.yong.pojo.EnrollmentQueryParam;
import com.yong.pojo.PageResult;

public interface EnrollmentService {

    // 分页查询
    PageResult<Enrollment> page(EnrollmentQueryParam enrollmentQueryParam);

    // 新增学生选课
    void save(Enrollment enrollment) throws Exception;

    // 删除学生选课
    void delete(List<Integer> ids);

    //根据id查询学生选课信息
    Enrollment getInfo(Integer id);

    //修改学生选课信息
    void update(Enrollment enrollment);

}
