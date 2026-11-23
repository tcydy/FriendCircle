package com.yong.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yong.pojo.Dept;
import com.yong.pojo.DeptQueryParam;

@Mapper
public interface DeptMapper {
    //查询
    public List<Dept> list(DeptQueryParam deptQueryParam);

    //删除院系
    public void delete(List<Integer> ids);

    //添加院系
    @Insert("insert into dept(name,office_location,create_time,update_time) values(#{name},#{officeLocation},#{createTime},#{updateTime})")
    void add(Dept dept);

    //根据id查询院系
    @Select("select id,name,office_location,create_time,update_time from dept where id=#{id}")
    Dept selectById(Integer id);

    //修改院系
    @Update("update dept set name=#{name},office_location=#{officeLocation},update_time=#{updateTime} where id=#{id}")
    void update(Dept dept);

    //返回院系查询下拉列表
    @MapKey("id")
    public List<Map<String, Object>> getDeptIdOptions();

}
