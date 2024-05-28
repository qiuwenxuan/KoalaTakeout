package com.example.entity;

import java.math.BigDecimal;

/**
 * 计价类
 **/
public class AmountDTO {
    // BigDecimal是Java中的一个类，用于表示任意精度的十进制数。它可以处理非常大或非常小的数字，以及需要高精度计算的场景

    /**
     * 总价
     **/
    private BigDecimal amount;
    /**
     * 优惠金额
     **/
    private BigDecimal discount;
    /**
     * 实际价格
     **/
    private BigDecimal actual;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getActual() {
        return actual;
    }

    public void setActual(BigDecimal actual) {
        this.actual = actual;
    }
}
