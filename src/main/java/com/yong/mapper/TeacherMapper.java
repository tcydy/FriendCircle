package com.yong.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import org.apache.ibatis.annotations.Update;

import com.yong.pojo.Teacher;
import com.yong.pojo.TeacherQueryParam;


//员工信息
@Mapper
public interface TeacherMapper {
    //查询
    public List<Teacher> list(TeacherQueryParam teacherQueryParam);

    //新增教师基本信息
    @Options(useGeneratedKeys=true,keyProperty="id")//获取到生成的主键
    @Insert("insert into teacher (name, image, entry_date,salary,title, dept_id, create_time, update_time) " +
            "values (#{name}, #{image}, #{entryDate},#{salary},#{title}, #{deptId}, #{createTime}, #{updateTime});")
    public void insert(Teacher teacher);

    //删除教师
    public void delete(List<Integer> ids);

    //根据id查询教师信息
    public Teacher getById(Integer id);

    //根据id修改教师基本信息
    public void updateById(Teacher teacher);

    //把老师的院系ID置为null
    public void updateByDeptId(List<Integer> ids);

    //返回教师查询下拉列表
    @MapKey("id")
    public List<Map<String, Object>> getTeacherIdOptions();

}
