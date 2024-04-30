package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.OrderStatusEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Comment;
import com.example.entity.Orders;
import com.example.mapper.CommentMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评价表业务处理
 **/
@Service
public class CommentService {

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private OrdersService ordersService;

    /**
     * 新增
     */
    @Transactional // 涉及到两个表之间的更新，需要将其包装成一个事务
    public void add(Comment comment) {
        // 设置评论时间
        comment.setTime(DateUtil.now());
        Orders orders = ordersService.selectById(comment.getOrderId());
        if (ObjectUtil.isNotEmpty(orders)) {
            // 设置评论的商家Id
            comment.setBusinessId(orders.getBusinessId());
            // 设置订单状态由 “待评论” 变成 “已完成”并更新
            orders.setStatus(OrderStatusEnum.DONE.getValue());
            ordersService.updateById(orders);
        }
        // 设置评论的当前用户
        Account currentUser = TokenUtils.getCurrentUser();
        comment.setUserId(currentUser.getId());
        commentMapper.insert(comment);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        commentMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            commentMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Comment comment) {
        commentMapper.updateById(comment);
    }

    /**
     * 根据ID查询
     */
    public Comment selectById(Integer id) {
        return commentMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Comment> selectAll(Comment comment) {
        // 拿到当前的登录用户信息
        Account currentUser = TokenUtils.getCurrentUser();
        String role = currentUser.getRole();
        if (RoleEnum.BUSINESS.name().equals(role)) { // 当角色是商家时，只能查询到商品分类的businessName为该商家的商品
            comment.setBusinessId(currentUser.getId());
        }
        return commentMapper.selectAll(comment);
    }

    /**
     * 分页查询
     */
    public PageInfo<Comment> selectPage(Comment comment, Integer pageNum, Integer pageSize) {
        // 拿到当前的登录用户信息
        Account currentUser = TokenUtils.getCurrentUser();
        String role = currentUser.getRole();
        if (RoleEnum.BUSINESS.name().equals(role)) { // 当角色是商家时，只能查询到商品分类的businessName为该商家的商品
            comment.setBusinessId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> list = commentMapper.selectAll(comment);
        return PageInfo.of(list);
    }

}