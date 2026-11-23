package com.yong.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yong.pojo.Enrollment;
import com.yong.pojo.EnrollmentQueryParam;
import com.yong.pojo.PageResult;
import com.yong.pojo.Result;
import com.yong.service.EnrollmentService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

//学生选课管理
@Slf4j
@RequestMapping("/enrollments")
@RestController
public class EnrollmentController{

    private final EnrollmentService enrollmentService;

    EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // 分页查询
    @GetMapping
    public Result page(EnrollmentQueryParam enrollmentQueryParam) {
        log.info("分页查询：{}", enrollmentQueryParam);
        PageResult<Enrollment> pageResult = enrollmentService.page(enrollmentQueryParam);
        return Result.success(pageResult);
    }

    // 新增学生选课
    @PostMapping
    public Result save(@RequestBody Enrollment enrollment) throws Exception {
        log.info("添加学生选课：{}", enrollment);
        enrollmentService.save(enrollment);
        return Result.success();
    }

    // 删除学生选课
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("删除学生选课：{}", ids);
        enrollmentService.delete(ids);
        return Result.success();
    }

    //根据id查询学生选课信息
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("根据id查询学生选课信息：{}", id);
        Enrollment enrollment = enrollmentService.getInfo(id);
        return Result.success(enrollment);
    }
    
    //修改学生选课信息
    @PutMapping
    public Result update(@RequestBody Enrollment enrollment) {
        log.info("修改学生选课：{}", enrollment);
        enrollmentService.update(enrollment);
        return Result.success();
    }

}
