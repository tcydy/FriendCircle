package com.yong.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yong.pojo.Course;
import com.yong.pojo.CourseQueryParam;
import com.yong.pojo.PageResult;
import com.yong.pojo.Result;
import com.yong.service.CourseService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RequestMapping("/courses")
@RestController
public class CourseController {

    private final CourseService courseService;

    CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // 分页查询
    @GetMapping
    public Result page(CourseQueryParam courseQueryParam) {
        log.info("分页查询：{}", courseQueryParam);
        PageResult<Course> pageResult = courseService.page(courseQueryParam);
        return Result.success(pageResult);
    }

    // 删除课程
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("根据ID删除课程：{}", ids);
        courseService.delete(ids);
        return Result.success();
    }

    // 添加课程
    @PostMapping
    public Result add(@RequestBody Course course) {
        log.info("添加课程：{}", course);
        courseService.add(course);
        return Result.success();
    }

    // 修改课程：查询回显，修改数据
    // 根据id查询课程
    @GetMapping("/{id}")
    public Result selectById(@PathVariable("id") Integer courseId) {
        log.info("根据id查询课程数据：{}", courseId);
        Course course = courseService.selectById(courseId);
        return Result.success(course);
    }

    // 修改课程
    @PutMapping
    public Result update(@RequestBody Course course) {
        log.info("修改课程：{}", course);
        courseService.update(course);
        return Result.success();
    }

    // 返回课程查询下拉列表
    @GetMapping("/courseIdOptions")
    public Result getCourseIdOptions() {
        log.info("课程查询下拉列表");
        List<Map<String, Object>> list = courseService.getCourseIdOptions();
        return Result.success(list);
    }

}
