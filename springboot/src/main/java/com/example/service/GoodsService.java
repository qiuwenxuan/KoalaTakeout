package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.OrderStatusEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.mapper.GoodsMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品信息业务处理
 **/
@Service
public class GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private BusinessService businessService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private OrdersItemService ordersItemService;
    @Resource
    private OrdersService ordersService;

    /**
     * 新增
     */
    public void add(Goods goods) {
        businessService.checkBusinessAuth(); // 当商家状态为“通过”，允许操作

        Category category = categoryService.selectById(goods.getCategoryId());
        if (ObjectUtil.isNotEmpty(category)) { // 因为Goods内含有categoryId和businessId,当goods赋予了商品分类categoryId时，由于商品分类属于商家，默认给商品good添加商家businessId
            goods.setBusinessId(category.getBusinessId());
        }
        goodsMapper.insert(goods);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        businessService.checkBusinessAuth(); // 当商家状态为“通过”，允许操作

        goodsMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        businessService.checkBusinessAuth(); // 当商家状态为“通过”，允许操作

        for (Integer id : ids) {
            goodsMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Goods goods) {
        businessService.checkBusinessAuth(); // 当商家状态为“通过”，允许操作

        goodsMapper.updateById(goods);
    }

    /**
     * 根据ID查询
     */
    public Goods selectById(Integer id) {
        Goods goods = goodsMapper.selectById(id);
        wrapGoods(goods); // 设置商品的打折之后的实际价格
        return goods;
    }

    /**
     * 查询所有
     */
    public List<Goods> selectAll(Goods goods) {
        Account currentUser = TokenUtils.getCurrentUser();
        String role = currentUser.getRole();
        if (RoleEnum.BUSINESS.name().equals(role)) { // 当角色是商家时，只能查询到自己的商品信息
            goods.setBusinessId(currentUser.getId());
        }
        List<Goods> goodsList = goodsMapper.selectAll(goods);
        // 循环设置商品打折后的价格
        for (Goods g : goodsList) {
            wrapGoods(g);
        }
        return goodsList;
    }

    /**
     * 分页查询
     */
    public PageInfo<Goods> selectPage(Goods goods, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        String role = currentUser.getRole();
        if (RoleEnum.BUSINESS.name().equals(role)) { // 当角色是商家时，只能查询到自己的商品信息
            goods.setBusinessId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> goodsList = goodsMapper.selectAll(goods);

        // 循环设置商品打折后的价格
        for (Goods g : goodsList) {
            wrapGoods(g);
        }
        return PageInfo.of(goodsList);
    }

    /**
     * 设置商品的实际价格 = 标准价格*打折优惠
     **/
    public Goods wrapGoods(Goods goods) {
        // 如果商品为空，返回null
        if (ObjectUtil.isEmpty(goods)) {
            return null;
        }
        int saleCount = 0; // 订单销售额
        // 设置商品实际价格 = 商品价格*打折优惠,保留两位小数向上取整
        BigDecimal actualPrice = goods.getPrice().multiply(BigDecimal.valueOf(goods.getDiscount())).setScale(2, RoundingMode.UP);
        goods.setActualPrice(actualPrice);

        List<OrdersItem> ordersItemList = ordersItemService.selectByGoodsId(goods.getId());
        // 定义一个list集合存储有效的OrdersItem(及状态为“待评价”/“已完成”)
        List<OrdersItem> usageOrdersItemList = new ArrayList<>();
        for (OrdersItem ordersItem : ordersItemList) {
            Integer orderId = ordersItem.getOrderId();
            Orders orders = ordersService.selectById(orderId);
            // 如果orders为空，则直接执行下一条循环
            if (ObjectUtil.isEmpty(orders)) {
                continue;
            }
            // 如果orders的状态为有效订单，则将ordersItem订单详情放到有效集合内
            if (OrderStatusEnum.NO_COMMENT.getValue().equals(orders.getStatus()) || OrderStatusEnum.DONE.getValue().equals(orders.getStatus())) {
                usageOrdersItemList.add(ordersItem);
            }
        }
        // 聚合函数查出有效订单集合usageOrdersItemList内的OrdersItem订单的商品数量
        saleCount += usageOrdersItemList.stream().map(OrdersItem::getNum).reduce(Integer::sum).orElse(0);
        goods.setSaleCount(saleCount);
        return goods;
    }

}