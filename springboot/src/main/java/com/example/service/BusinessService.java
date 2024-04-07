package com.example.service;

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
import java.util.Objects;

/**
 * 商家相关的业务方法
 */
@Service
public class BusinessService {

    @Resource
    private BusinessMapper businessMapper;

    /**
     * 新增商家
     */
    public void add(Business business) {
        if (ObjectUtil.isEmpty(business.getUsername()) || ObjectUtil.isEmpty(business.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        Business dbBusiness = this.selectByUsername(business.getUsername());
        // 如果根据新增数据的账号查询查到了数据  那么这个数据不允许插入，因为账号不能重复
        if (ObjectUtil.isNotEmpty(dbBusiness)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        business.setRole(RoleEnum.BUSINESS.name());
        businessMapper.insert(business);
    }

    /**
     * 单个删除
     */
    public void deleteById(Integer id) {
        businessMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 修改商家
     */
    public void updateById(Business business) {
        // 先根据id查询商家是否存在，商家不存在那就返回错误信息
        Business dbBusiness1 = selectById(business.getId());
        if (ObjectUtil.isEmpty(dbBusiness1)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        Business dbBusiness2 = this.selectByUsername(business.getUsername());
        // 如果当前修改的用户查询到数据库中存在名称相同，但是id不同的用户，那么当前的更新是不合法的及数据重复了（id为主键不相同，但是用户名称可能相同）
        if (ObjectUtil.isNotEmpty(dbBusiness2) && !Objects.equals(dbBusiness2.getId(), business.getId())) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        businessMapper.updateById(business);
    }

    /**
     * 查询所有
     */
    public List<Business> selectAll(Business business) {
        return businessMapper.selectAll(business);
    }

    /**
     * 根据账号查询
     */
    public Business selectByUsername(String username) {
        Business params = new Business();
        params.setUsername(username);
        List<Business> list = this.selectAll(params);
        return list.size() == 0 ? null : list.get(0);
    }

    /**
     * 根据ID查询
     */
    public Business selectById(Integer id) {
        Business params = new Business();
        params.setId(id);
        List<Business> list = this.selectAll(params);
        return list.size() == 0 ? null : list.get(0);
    }

    /**
     * 分页条件查询
     */
    public PageInfo<Business> selectPage(Business business, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);  // 调用PageHelper插件配置分页查询的规则
        List<Business> list = businessMapper.selectAll(business);  // 获取所有查询的数据
        return PageInfo.of(list);  // 将查询的数据返回到分页查询规则的方法内返回数据
    }

}