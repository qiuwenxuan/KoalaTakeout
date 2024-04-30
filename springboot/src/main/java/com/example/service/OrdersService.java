package com.example.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.OrderStatusEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.OrdersMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 业务处理
 **/
@Service
public class OrdersService {

    private static final Logger log = LoggerFactory.getLogger(OrdersService.class);

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private CartService cartService;
    @Resource
    private OrdersItemService ordersItemService;


    /**
     * 新增
     */
    public void add(Orders orders) {
        ordersMapper.insert(orders);
    }

    /**
     * 删除订单表数据，也要删除订单订单详情表内的数据
     */
    @Transactional
    public void deleteById(Integer id) {
        // 删除订单表的相同id的订单
        ordersMapper.deleteById(id);
        // 删除订单详情表的所有订单
        ordersItemService.deleteByOrderId(id);
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
     * 修改
     */
    public void updateById(Orders orders) {
        // 支付时设置支付的时间
        if (OrderStatusEnum.NO_SEND.getValue().equals(orders.getStatus())) {
            orders.setPayTime(DateUtil.now());
        }
        ordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Orders> selectAll(Orders orders) {
        // 拿到当前的登录用户信息
        Account currentUser = TokenUtils.getCurrentUser();
        String role = currentUser.getRole();
        if (RoleEnum.BUSINESS.name().equals(role)) { // 当角色是商家时，只能查询到自商家的订单
            orders.setBusinessId(currentUser.getId());
        }
        return ordersMapper.selectAll(orders);
    }

    /**
     * 分页查询
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {
        // 拿到当前的登录用户信息
        Account currentUser = TokenUtils.getCurrentUser();
        String role = currentUser.getRole();
        if (RoleEnum.BUSINESS.name().equals(role)) { // 当角色是商家时，只能查询到自商家的订单
            orders.setBusinessId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = ordersMapper.selectAll(orders);
        return PageInfo.of(list);
    }

    /**
     * 添加订单
     **/
    @Transactional // Transactional表示将方法包装成一个事务
    public void addOrder(OrdersDTO ordersDTO) {
        Integer businessId = ordersDTO.getBusinessId();
        Account currentUser = TokenUtils.getCurrentUser();
        Integer userId = currentUser.getId();

        if (ObjectUtil.isEmpty(currentUser.getId())) {
            throw new CustomException(ResultCodeEnum.USER_NOAUTH_ERROR);
        }
        List<Cart> cartList = cartService.selectUserCart(currentUser.getId(), businessId);
        if (CollUtil.isEmpty(cartList)) {
            throw new CustomException(ResultCodeEnum.CART_EMPTY_ERROR);
        }

        Orders orders = new Orders();
        orders.setBusinessId(businessId);
        String time = DateUtil.now();
        orders.setTime(time);
        orders.setPayType(ordersDTO.getPayType());
        orders.setUserId(userId);
        orders.setAddress(ordersDTO.getAddress());
        orders.setUser(ordersDTO.getUser());
        orders.setPhone(ordersDTO.getPhone());
        orders.setComment(ordersDTO.getComment());

        // 设置商品信息
        AmountDTO amountDTO = cartService.calc(userId, businessId);
        orders.setAmount(amountDTO.getAmount());
        orders.setDiscount(amountDTO.getDiscount());
        orders.setActual(amountDTO.getActual());

        // 得到购物车商品的总数
        Integer nums = cartList.stream().map(Cart::getNum).reduce(Integer::sum).orElse(0);
        orders.setName(cartList.get(0).getGoods().getName() + "等" + nums + "件商品");
        orders.setCover(cartList.get(0).getGoods().getImg());

        // 最后设置一个订单编号
        orders.setOrderNo(IdUtil.getSnowflakeNextIdStr());  // 雪花算法生成唯一的ID作为订单号
        // 设置订单状态
        orders.setStatus(OrderStatusEnum.NO_PAY.getValue());
        this.add(orders);

        // 再设置订单的 详细信息
        for (Cart cart : cartList) {
            OrdersItem ordersItem = new OrdersItem();
            ordersItem.setOrderId(orders.getId());
            ordersItem.setGoodsName(cart.getGoods().getName());
            ordersItem.setGoodsImg(cart.getGoods().getImg());
            ordersItem.setPrice(cart.getGoods().getActualPrice());
            ordersItem.setNum(cart.getNum());
            ordersItemService.add(ordersItem);
        }

        // 清空购物车
        cartService.deleteByBusiness(businessId, userId);

    }


}