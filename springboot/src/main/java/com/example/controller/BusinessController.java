package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Business;
import com.example.service.BusinessService;
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
     * 添加数据
     **/
    @PostMapping("/add")
    public Result add(@RequestBody Business business) {
        // 如果商家username和password为空，则抛出参数缺失错误
        if (ObjectUtil.isEmpty(business.getUserName()) || ObjectUtil.isEmpty(business.getPassWord())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        businessService.insert(business);  // 添加操作没有返回值，直接返回成功信息
        return Result.success(business.getName() + "添加成功！");
    }
}
