package com.example.controller;

import com.example.common.Result;
import com.example.entity.Business;
import com.example.service.BusinessService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商家管理相关接口
 **/
@RestController
@RequestMapping("/business")
public class BusinessController {

    @Resource
    private BusinessService businessService;

    /**
     * 查询所有
     **/
    @GetMapping("/selectAll")
    public Result selectAll(Business business) {  // selectAll接口，如果没有传参则查询所有数据，如果传入参数如id/name则查询单一数据
        List<Business> list = businessService.selectAll(business);
        return Result.success(list);
    }

    /**
     * 通过Id查询
     **/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Business business = businessService.selectById(id);
        return Result.success(business);
    }

    /**
     * 通过username查询
     **/
    @GetMapping("/selectByUsername")
    public Result selectByUsername(@RequestParam String username) {
        Business business = businessService.selectByUsername(username);
        return Result.success(business);
    }

    /**
     * 删除商家
     **/
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        businessService.deleteById(id);
        return Result.success("删除成功！");
    }

    /**
     * 批量删除
     **/
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        businessService.deleteBatch(ids);
        return Result.success("批量删除成功！");
    }

    /**
     * 更新操作
     **/
    @PutMapping("/update/{id}")
    public Result updateById(@RequestBody Business business) {
        businessService.updateById(business);
        return Result.success("更新操作成功！");
    }

    /**
     * 添加数据
     **/
    @PostMapping("/add")
    public Result add(@RequestBody Business business) {
        businessService.insert(business);  // 添加操作没有返回值，直接返回成功信息
        return Result.success("添加成功！");
    }

    /**
     * 分页条件查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Business business,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Business> pageInfo = businessService.selectPage(business, pageNum, pageSize);
        return Result.success(pageInfo);
    }

}
