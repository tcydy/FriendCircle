package com.yong.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yong.pojo.Dept;
import com.yong.pojo.DeptQueryParam;
import com.yong.pojo.PageResult;
import com.yong.pojo.Result;
import com.yong.service.DeptService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RequestMapping("/depts")
@RestController
public class DeptController {

    private final DeptService deptService;

    DeptController(DeptService deptService) {
        this.deptService = deptService;
    }
 
    // 分页查询
    @GetMapping
    public Result page(DeptQueryParam deptQueryParam) {
        log.info("分页查询：{}", deptQueryParam);
        PageResult<Dept> pageResult = deptService.page(deptQueryParam);
        return Result.success(pageResult);
    }

    // 删除院系
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("根据ID删除院系：{}",ids);
        deptService.delete(ids);
        return Result.success();
    }

    //添加院系
    @PostMapping
    public Result add(@RequestBody Dept dept) {
        log.info("添加院系：{}" , dept);
        deptService.add(dept);
        return Result.success();
    }
    
    //修改院系：查询回显，修改数据
    //根据id查询院系
    @GetMapping("/{id}")
    public Result selectById(@PathVariable("id") Integer deptId) {
        log.info("根据id查询院系数据：{}" , deptId);
        Dept dept = deptService.selectById(deptId);
        return Result.success(dept);
    }

    //修改院系
    @PutMapping
    public Result update(@RequestBody Dept dept) {
        log.info("修改院系：{}", dept);
        deptService.update(dept);
        return Result.success();
    }

    //返回院系查询下拉列表
    @GetMapping("/deptIdOptions")
    public Result getDeptIdOptions() {
        log.info("院系查询下拉列表");
        List<Map<String, Object>> list = deptService.getDeptIdOptions();
        return Result.success(list);
    }
    
}
