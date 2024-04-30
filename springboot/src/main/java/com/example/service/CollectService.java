package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Business;
import com.example.entity.Collect;
import com.example.mapper.CollectMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 业务处理
 **/
@Service
public class CollectService {

    @Resource
    private CollectMapper collectMapper;
    @Resource
    private BusinessService businessService;

    /**
     * 新增
     */
    public void add(Collect collect) {
        collectMapper.insert(collect);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        collectMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            collectMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Collect collect) {
        collectMapper.updateById(collect);
    }

    /**
     * 根据ID查询
     */
    public Collect selectById(Integer id) {
        return collectMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Collect> selectAll(Collect collect) {
        List<Collect> collects = collectMapper.selectAll(collect);
        // 给系统business属性赋值
        for (Collect c : collects) {
            Business business = businessService.selectById(c.getBusinessId());
            c.setBusiness(business);
        }
        return collects;
    }

    /**
     * 分页查询
     */
    public PageInfo<Collect> selectPage(Collect collect, Integer pageNum, Integer pageSize) {
        // 拿到当前的登录用户信息
        Account currentUser = TokenUtils.getCurrentUser();
        String role = currentUser.getRole();
        if (RoleEnum.BUSINESS.name().equals(role)) { // 当角色是商家时，只能查询到当前商家的收藏店铺信息
            collect.setBusinessId(currentUser.getId());
        }
        // 一定要权限隔离在前，然后分页查询在后
        PageHelper.startPage(pageNum, pageSize);
        List<Collect> list = collectMapper.selectAll(collect);
        return PageInfo.of(list);
    }

    /**
     * 根据userId和businessId查询收藏表
     **/
    public Collect selectByUserIdAndBusinessId(Integer userId, Integer businessId) {
        return collectMapper.selectByUserIdAndBusinessId(userId, businessId);
    }

    /**
     * 收藏/取消收藏 商家
     **/
    public void saveCollect(Collect collect) {
        Collect dbCollect = this.selectByUserIdAndBusinessId(collect.getUserId(), collect.getBusinessId());
        // 如果查询到用户收藏过该商家，则执行取消收藏
        if (ObjectUtil.isNotEmpty(dbCollect)) {
            this.deleteById(dbCollect.getId()); // 删除收藏
        } else {
            // 如果查询到用户没有收藏过该商家，则执行新增收藏
            collect.setTime(DateUtil.now()); // 设置收藏时间
            this.add(collect); // 添加收藏
        }
    }
}