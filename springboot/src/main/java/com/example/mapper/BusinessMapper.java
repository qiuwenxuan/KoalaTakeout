package com.example.mapper;

import com.example.entity.Business;

import java.util.List;

/**
 * 商家相关的数据访问接口
 **/
public interface BusinessMapper {
    /**
     * 查询所有
     **/
    List<Business> selectAll(Business business);

    /**
     * 新增数据
     **/
    int insert(Business business); // 新增数据返回int类型的id

    int deleteById(Integer id); // 通过id删除数据

    int updateById(Business business);
}
