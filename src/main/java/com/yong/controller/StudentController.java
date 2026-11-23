package com.yong.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yong.pojo.Student;
import com.yong.pojo.StudentQueryParam;
import com.yong.pojo.PageResult;
import com.yong.pojo.Result;
import com.yong.service.StudentService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

//学生管理
@Slf4j
@RequestMapping("/students")
@RestController
public class StudentController {

    private final StudentService studentService;

    StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 分页查询
    @GetMapping
    public Result page(StudentQueryParam studentQueryParam) {
        log.info("分页查询：{}", studentQueryParam);
        PageResult<Student> pageResult = studentService.page(studentQueryParam);
        return Result.success(pageResult);
    }

    // 新增学生
    @PostMapping
    public Result save(@RequestBody Student student) throws Exception {
        log.info("添加学生：{}", student);
        studentService.save(student);
        return Result.success();
    }

    // 删除学生
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("删除学生：{}", ids);
        studentService.delete(ids);
        return Result.success();
    }

    //根据id查询学生信息
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("根据id查询学生信息：{}", id);
        Student student = studentService.getInfo(id);
        return Result.success(student);
    }
    
    //修改学生信息
    @PutMapping
    public Result update(@RequestBody Student student) {
        log.info("修改学生：{}", student);
        studentService.update(student);
        return Result.success();
    }

    //返回学生查询下拉列表
    @GetMapping("/studentIdOptions")
    public Result getStudentIdOptions() {
        log.info("学生查询下拉列表");
        List<Map<String, Object>> list = studentService.getStudentIdOptions();
        return Result.success(list);
    }

}
