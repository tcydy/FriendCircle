package com.yong.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yong.pojo.Clss;
import com.yong.pojo.ClssQueryParam;
import com.yong.pojo.PageResult;
import com.yong.pojo.Result;
import com.yong.service.ClssService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RequestMapping("/clsss")
@RestController
public class ClssController {

    private final ClssService clssService;

    ClssController(ClssService clssService) {
        this.clssService = clssService;
    }

    // 分页查询
    @GetMapping
    public Result page(ClssQueryParam clssQueryParam) {
        log.info("分页查询：{}", clssQueryParam);
        PageResult<Clss> pageResult = clssService.page(clssQueryParam);
        return Result.success(pageResult);
    }

    // 删除班级
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("根据ID删除班级：{}",ids);
        clssService.delete(ids);
        return Result.success();
    }

    //添加班级
    @PostMapping
    public Result add(@RequestBody Clss clss) {
        log.info("添加班级：{}" , clss);
        clssService.add(clss);
        return Result.success();
    }
    
    //修改班级：查询回显，修改数据
    //根据id查询班级
    @GetMapping("/{id}")
    public Result selectById(@PathVariable("id") Integer clssId) {
        log.info("根据id查询班级数据：{}" , clssId);
        Clss clss = clssService.selectById(clssId);
        return Result.success(clss);
    }

    //修改班级
    @PutMapping
    public Result update(@RequestBody Clss clss) {
        log.info("修改班级：{}", clss);
        clssService.update(clss);
        return Result.success();
    }

    //返回班级查询下拉列表
    @GetMapping("/classIdOptions")
    public Result getClassIdOptions() {
        log.info("班级查询下拉列表");
        List<Map<String, Object>> list = clssService.getClassIdOptions();
        return Result.success(list);
    }


}
