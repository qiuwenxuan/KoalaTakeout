package com.example.entity;

public class Cart {
    /**
     * ID
     */
    private Integer id;
    /**
     * 商品ID
     */
    private Integer goodsId;
    /**
     * 数量
     */
    private Integer num;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 商家id
     **/
    private Integer businessId;
    /**
     * 商品信息
     **/
    private Goods goods;
    /**
     * 商家信息
     **/
    private Business business;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }
}