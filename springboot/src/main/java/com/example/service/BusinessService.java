package com.example.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Business;
import com.example.exception.CustomException;
import com.example.mapper.BusinessMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BusinessService {

    @Resource
    private BusinessMapper businessMapper;

    public List<Business> selectAll(Business business) {
        return businessMapper.selectAll(business);
    }

    /**
     * 添加数据
     **/
    public void insert(Business business) {
        // 如果商家username和password为空，则抛出参数缺失错误
        if (ObjectUtil.isEmpty(business.getUsername()) || ObjectUtil.isEmpty(business.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        // 创建一个空的Business对象
        Business params = new Business();
        params.setUsername(business.getUsername());
        // 调用selectAll()方法查询是否含有username，如果用户username已存在，则无法添加相同用户名的数据，且抛出异常
        List<Business> list = this.selectAll(params);
        if (CollUtil.isNotEmpty(list)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        business.setRole(RoleEnum.BUSINESS.name());
        businessMapper.insert(business);
    }

    /**
     * 删除数据
     **/
    public void deleteById(Integer id) {
        businessMapper.deleteById(id);
    }

    /**
     * 批量删除
     **/
    public void deleteBatch(List<Integer> ids) {
        // 使用循环调用deleteById方法批量删除数据
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 更新操作
     **/
    public void updateById(Business business) {
        if (ObjectUtil.isEmpty(business.getId()) || ObjectUtil.isEmpty(business.getPassword()) || ObjectUtil.isEmpty(business.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        List<Business> list = selectAll(business);
        if (CollUtil.isEmpty(list)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        businessMapper.updateById(business);
    }

    /**
     * 根据id查询数据
     **/
    public Business selectById(Integer id) {
        // 参数为空，抛出缺失参数异常
        if (ObjectUtil.isEmpty(id)) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        Business param = new Business();
        param.setId(id);
        List<Business> list = selectAll(param);
        // 如果数据库查询的id不存在
        if (CollUtil.isEmpty(list)) {
            throw new CustomException(ResultCodeEnum.BUSINESS_EXIST_ERROR);
        }
        return list.get(0);
    }

    /**
     * 根据username查询
     **/
    public Business selectByUsername(String username) {
        // 参数为空，抛出缺失参数异常
        if (ObjectUtil.isEmpty(username)) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        Business param = new Business();
        param.setUsername(username);
        List<Business> list = selectAll(param);
        // 如果数据库查询的id不存在
        if (CollUtil.isEmpty(list)) {
            throw new CustomException(ResultCodeEnum.BUSINESS_EXIST_ERROR);
        }
        return list.get(0);
    }

    /**
     * 分页条件查询
     */
    public PageInfo<Business> selectPage(Business business, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Business> list = businessMapper.selectAll(business);
        return PageInfo.of(list);
    }
}
