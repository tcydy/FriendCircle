package com.yong.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yong.pojo.Teaching;
import com.yong.pojo.TeachingQueryParam;
import com.yong.pojo.PageResult;
import com.yong.pojo.Result;
import com.yong.service.TeachingService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

//授课安排管理
@Slf4j
@RequestMapping("/teachings")
@RestController
public class TeachingController{

    private final TeachingService teachingService;

    TeachingController(TeachingService teachingService) {
        this.teachingService = teachingService;
    }

    // 分页查询
    @GetMapping
    public Result page(TeachingQueryParam teachingQueryParam) {
        log.info("分页查询：{}", teachingQueryParam);
        PageResult<Teaching> pageResult = teachingService.page(teachingQueryParam);
        return Result.success(pageResult);
    }

    // 新增授课安排
    @PostMapping
    public Result save(@RequestBody Teaching teaching) throws Exception {
        log.info("添加授课安排：{}", teaching);
        teachingService.save(teaching);
        return Result.success();
    }

    // 删除授课安排
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("删除授课安排：{}", ids);
        teachingService.delete(ids);
        return Result.success();
    }

    //根据id查询授课安排信息
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("根据id查询授课安排信息：{}", id);
        Teaching teaching = teachingService.getInfo(id);
        return Result.success(teaching);
    }
    
    //修改授课安排信息
    @PutMapping
    public Result update(@RequestBody Teaching teaching) {
        log.info("修改授课安排：{}", teaching);
        teachingService.update(teaching);
        return Result.success();
    }

}
