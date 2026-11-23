package com.yong.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import com.yong.pojo.Clss;
import com.yong.pojo.ClssQueryParam;

@Mapper
public interface ClssMapper {
    //查询
    public List<Clss> list(ClssQueryParam clssQueryParam);

    //删除对应id的班级数据
    void delete(List<Integer> ids);

    //添加班级
    @Insert("insert into clss(name,dept_id,create_time,update_time) values(#{name},#{deptId},#{createTime},#{updateTime})")
    void add(Clss clss);

    //根据id查询班级
    Clss selectById(Integer id);

    //修改班级
    @Update("update clss set name=#{name},dept_id=#{deptId},update_time=#{updateTime} where id=#{id}")
    void update(Clss clss);

    //把班级的院系ID置为null
    @Update("update clss set dept_id = null where dept_id= #{deptId}")
    public void updateByDeptId(Integer deptId);

    //返回班级查询下拉列表
    @MapKey("id")
    public List<Map<String, Object>> getClassIdOptions();

}
