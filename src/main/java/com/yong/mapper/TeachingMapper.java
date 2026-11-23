package com.yong.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import com.yong.pojo.Teaching;
import com.yong.pojo.TeachingQueryParam;

@Mapper
public interface TeachingMapper {
    //查询
    public List<Teaching> list(TeachingQueryParam teachingQueryParam);

    //新增授课安排基本信息
    @Options(useGeneratedKeys=true,keyProperty="id")//获取到生成的主键
    @Insert("insert into teaching (teacher_id,course_id,semester) " +
            "values (#{teacherId}, #{courseId}, #{semester});")
    public void insert(Teaching teaching);

    //删除授课安排
    public void delete(List<Integer> ids);

    //根据id查询授课安排信息
    public Teaching getById(Integer id);

    //根据id修改授课安排基本信息
    public void updateById(Teaching teaching);

    //根据courseId删除
    public void deleteByCourseId(List<Integer> ids);

    //根据teacherId删除
    @Delete("delete from teaching where teacher_id= #{teacherId}")
    public void deleteByTeacherId(Integer teacherId);

    //根据teacherId批量删除
    public void batchDeleteByTeacherId (List<Integer> ids);

}
 