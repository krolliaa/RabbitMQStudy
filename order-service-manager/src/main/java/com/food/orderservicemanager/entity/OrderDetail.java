package com.food.orderservicemanager.entity;

import com.food.orderservicemanager.enummeration.OrderStatus;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单详情实体类
 */
public class OrderDetail {
    /**
     * 订单 ID
     */
    private Integer id;
    /**
     * 订单状态
     * 使用枚举类获取订单状态
     */
    private OrderStatus status;
    /**
     * 配送地址
     */
    private String address;
    /**
     * 用户 ID
     */
    private Integer accountId;
    /**
     * 商品 ID
     */
    private Integer productId;
    /**
     * 已分配骑手 ID
     */
    private Integer deliverymanId;
    /**
     * 结算 ID
     */
    private Integer settlementId;
    /**
     * 积分 ID
     */
    private Integer rewardId;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 订单日期
     */
    private Date date;
}
