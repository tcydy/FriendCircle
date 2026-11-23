package com.yong.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yong.pojo.Course;
import com.yong.pojo.CourseQueryParam;

@Mapper
public interface CourseMapper {
    //查询
    public List<Course> list(CourseQueryParam courseQueryParam);

    //删除对应id的课程数据
    void delete(List<Integer> ids);

    //添加课程
    @Insert("insert into course(name,credits,create_time,update_time) values(#{name},#{credits},#{createTime},#{updateTime})")
    void add(Course course);

    //根据id查询课程
    @Select("select id,name,credits,create_time,update_time from course where id=#{id}")
    Course selectById(Integer id);

    //修改课程
    @Update("update course set name=#{name},credits=#{credits},update_time=#{updateTime} where id=#{id}")
    void update(Course course);

    //返回学生查询下拉列表
    @MapKey("id")
    public List<Map<String, Object>> getCourseIdOptions();

}
