package com.example.service;

import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Category;
import com.example.mapper.CategoryMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品分类业务处理
 **/
@Service
public class CategoryService {

    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private BusinessService businessService;

    /**
     * 新增
     */
    public void add(Category category) {
        businessService.checkBusinessAuth(); // 当商家状态为“通过”，允许操作
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) { // 如果当前角色为商家，添加数据默认给商品分类的business赋初始值
            category.setBusinessId(currentUser.getId());
        }
        categoryMapper.insert(category);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        businessService.checkBusinessAuth(); // 当商家状态为“通过”，允许操作
        categoryMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        businessService.checkBusinessAuth(); // 当商家状态为“通过”，允许操作
        for (Integer id : ids) {
            categoryMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Category category) {
        businessService.checkBusinessAuth(); // 当商家状态为“通过”，允许操作
        categoryMapper.updateById(category);
    }

    /**
     * 根据ID查询
     */
    public Category selectById(Integer id) {
        return categoryMapper.selectById(id);
    }

    /**
     * 查询所有(重要)
     */
    public List<Category> selectAll(Category category) {
        // 拿到当前的登录用户信息
        Account currentUser = TokenUtils.getCurrentUser();
        String role = currentUser.getRole();
        if (RoleEnum.BUSINESS.name().equals(role)) { // 当角色是商家时，只能查询到商品分类的businessName为该商家的商品
            category.setBusinessId(currentUser.getId());
        }
        return categoryMapper.selectAll(category);
    }

    /**
     * 分页查询
     */
    public PageInfo<Category> selectPage(Category category, Integer pageNum, Integer pageSize) {
        // 分页查询也需要遵守当前用户角色维管理员，只能查询商品businessName为当前商家的商品分类
        Account currentUser = TokenUtils.getCurrentUser();
        String role = currentUser.getRole();
        if (RoleEnum.BUSINESS.name().equals(role)) { // 当角色是商家时，只能查询到商品分类的businessName为该商家的商品
            category.setBusinessId(currentUser.getId());
        }

        PageHelper.startPage(pageNum, pageSize);

        List<Category> list = categoryMapper.selectAll(category);
        return PageInfo.of(list);
    }


}