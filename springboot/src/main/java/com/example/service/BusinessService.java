package com.example.service;

import cn.hutool.core.collection.CollUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Business;
import com.example.exception.CustomException;
import com.example.mapper.BusinessMapper;
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
        // 创建一个空的Business对象
        Business params = new Business();
        params.setUserName(business.getUserName());
        // 调用selectAll()方法查询是否含有username，如果用户username已存在，则无法添加相同用户名的数据，且抛出异常
        List<Business> list = this.selectAll(params);
        if (CollUtil.isNotEmpty(list)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        business.setRole(RoleEnum.BUSINESS.name());
        businessMapper.insert(business);
    }

}
