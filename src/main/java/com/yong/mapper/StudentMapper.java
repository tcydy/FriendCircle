package com.yong.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import com.yong.pojo.Student;
import com.yong.pojo.StudentQueryParam;

//学生信息
@Mapper
public interface StudentMapper {
    //查询
    public List<Student> list(StudentQueryParam studentQueryParam);

    //新增学生基本信息
    @Options(useGeneratedKeys=true,keyProperty="id")//获取到生成的主键
    @Insert("insert into student (name, gender, image, enrollment_year, dept_id, class_id, create_time, update_time) " +
            "values (#{name}, #{gender}, #{image}, #{enrollmentYear}, #{deptId}, #{classId},#{createTime}, #{updateTime});")
    public void insert(Student student);

    //删除学生
    public void delete(List<Integer> ids);

    //根据id查询学生信息
    public Student getById(Integer id);

    //根据id修改学生基本信息
    public void updateById(Student student);

    //把学生的班级ID置为null
    public void updateByClassId(List<Integer> ids);

     //把学生的院系ID置为null
     public void updateByDeptId(List<Integer> ids);
    
    //返回学生查询下拉列表
    @MapKey("id")
    public List<Map<String, Object>> getStudentIdOptions();
}
