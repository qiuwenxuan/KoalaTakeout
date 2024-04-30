package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.AmountDTO;
import com.example.entity.Business;
import com.example.entity.Cart;
import com.example.entity.Goods;
import com.example.mapper.CartMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车业务处理
 **/
@Service
public class CartService {

    @Resource
    private CartMapper cartMapper;
    @Resource
    private GoodsService goodsService;
    @Resource
    private BusinessService businessService;

    /**
     * 新增
     */
    public void add(Cart cart) {
        cartMapper.insert(cart);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        cartMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            cartMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Cart cart) {
        cartMapper.updateById(cart);
    }

    /**
     * 根据ID查询
     */
    public Cart selectById(Integer id) {
        // 调用查询接口，给cart.goods、business属性赋值并一起返回
        Cart cart = cartMapper.selectById(id);
        Goods goods = goodsService.selectById(cart.getGoodsId());
        Business business = businessService.selectById(cart.getBusinessId());
        cart.setGoods(goods);
        cart.setBusiness(business);
        return cart;
    }

    /**
     * 查询所有
     */
    public List<Cart> selectAll(Cart cart) {
        // 设置用户购物车内的所有商品信息
        List<Cart> carts = cartMapper.selectAll(cart);
        for (Cart c : carts) {
            Goods goods = goodsService.selectById(c.getGoodsId());
            Business business = businessService.selectById(c.getBusinessId());
            c.setGoods(goods);
            c.setBusiness(business);
        }
        return carts;
    }

    /**
     * 分页查询
     */
    public PageInfo<Cart> selectPage(Cart cart, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Cart> list = selectAll(cart);
        return PageInfo.of(list);
    }

    /**
     * 计算购物车金额方法
     **/
    public AmountDTO calc(Integer userId, Integer businessId) {
        // 定义一个cart对象，存储userId，businessId用于查出当前用户在某商家的所有购物车商品数据
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setBusinessId(businessId);
        // 查出当前用户在某商家的所有购物车商品数据
        List<Cart> cartList = this.selectAll(cart);
        // 定义三个金额计算变量
        BigDecimal amount = BigDecimal.ZERO; // 商品原价
        BigDecimal discount = BigDecimal.ZERO; // 商品优惠金额
        BigDecimal actual = BigDecimal.ZERO; // 商品打折之后价格

        for (Cart c : cartList) {
            Goods goods = c.getGoods();
            if (ObjectUtil.isNotEmpty(goods)) {
                BigDecimal price = goods.getPrice();
                BigDecimal actualPrice = goods.getActualPrice();
                amount = amount.add(price.multiply(BigDecimal.valueOf(c.getNum()))); // 商品原价累加
                actual = actual.add(actualPrice.multiply(BigDecimal.valueOf(c.getNum()))); // 商品打折之后价格累加
            }
        }
        AmountDTO amountDTO = new AmountDTO();
        amountDTO.setAmount(amount);
        amountDTO.setActual(actual);
        discount = amount.subtract(actual);
        amountDTO.setDiscount(discount); // 商品优惠金额 = （商品原价-打折之后的价格）
        return amountDTO;
    }

    /**
     * 删除某用户在某商家下的购物车内所有商品信息
     *
     **/
    public void deleteByBusiness(Integer businessId, Integer userId) {
        cartMapper.deleteByBusiness(businessId, userId);
    }

    /**
     * 查询某用户在某商家下的所有购物车内商品信息
     **/
    public List<Cart> selectUserCart(Integer userId, Integer businessId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setBusinessId(businessId);
        return this.selectAll(cart);
    }
}