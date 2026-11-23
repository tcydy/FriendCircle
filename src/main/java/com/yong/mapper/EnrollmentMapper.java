package com.yong.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import com.yong.pojo.Enrollment;
import com.yong.pojo.EnrollmentQueryParam;


@Mapper
public interface EnrollmentMapper {
    //查询
    public List<Enrollment> list(EnrollmentQueryParam enrollmentQueryParam);

    //新增学生选课基本信息
    @Options(useGeneratedKeys=true,keyProperty="id")//获取到生成的主键
    @Insert("insert into enrollment (student_id,course_id,semester) " +
            "values (#{studentId}, #{courseId}, #{semester});")
    public void insert(Enrollment enrollment);

    //删除学生选课
    public void delete(List<Integer> ids);

    //根据id查询学生选课信息
    public Enrollment getById(Integer id);

    //根据id修改学生选课基本信息
    public void updateById(Enrollment enrollment);

    //根据courseId删除)
    public void deleteByCourseId(List<Integer> ids);

    //根据studentId删除
    @Delete("delete from enrollment where student_id= #{studentId}")
    public void deleteByStudentId(Integer studentId);

    //根据studentId批量删除
    public void batchDeleteByStudentId(List<Integer> ids);

}
