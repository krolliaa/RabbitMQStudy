package com.food.orderservicemanager.enummeration;

public enum OrderStatus {
    /**
     * 订单创建中
     */
    ORDER_CREATING,
    /**
     * 订单创建完成
     */
    ORDER_CREATED,
    /**
     * 订单创建失败
     */
    ORDER_FAILED,
    /**
     * 商家确认订单
     */
    RESTAURANT_CONFIRMED,
    /**
     * 骑手确认订单
     */
    DELIVERYMAN_CONFIRMED,
    /**
     * 订单完成结算确认
     */
    SETTLEMENT_CONFIRMED
}
