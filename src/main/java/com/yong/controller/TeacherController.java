package com.yong.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yong.pojo.Teacher;
import com.yong.pojo.TeacherQueryParam;
import com.yong.pojo.PageResult;
import com.yong.pojo.Result;
import com.yong.service.TeacherService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

//教师管理
@Slf4j
@RequestMapping("/teachers")
@RestController
public class TeacherController {

    private final TeacherService teacherService;

    TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    // 分页查询
    @GetMapping
    public Result page(TeacherQueryParam teacherQueryParam) {
        log.info("分页查询：{}", teacherQueryParam);
        PageResult<Teacher> pageResult = teacherService.page(teacherQueryParam);
        return Result.success(pageResult);
    }

    // 新增教师
    @PostMapping
    public Result save(@RequestBody Teacher teacher) throws Exception {
        log.info("添加教师：{}", teacher);
        teacherService.save(teacher);
        return Result.success();
    }

    // 删除教师
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("删除教师：{}", ids);
        teacherService.delete(ids);
        return Result.success();
    }

    //根据id查询教师信息
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("根据id查询教师信息：{}", id);
        Teacher teacher = teacherService.getInfo(id);
        return Result.success(teacher);
    }
    
    //修改教师信息
    @PutMapping
    public Result update(@RequestBody Teacher teacher) {
        log.info("修改教师：{}", teacher);
        teacherService.update(teacher);
        return Result.success();
    }

    //返回教师查询下拉列表
    @GetMapping("/teacherIdOptions")
    public Result getTeacherIdOptions() {
        log.info("教师查询下拉列表");
        List<Map<String, Object>> list = teacherService.getTeacherIdOptions();
        return Result.success(list);
    }

}
