package com.food.orderservicemanager.dto;

import com.food.orderservicemanager.enummeration.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessageDTO {
    //包括订单ID 用户ID 商家ID 产品ID 骑手ID 结算ID 积分ID 订单价格 状态积分数量 确认状态
    private Integer orderId;
    private OrderStatus orderStatus;
    private BigDecimal price;
    private Integer deliverymanId;
    private Integer productId;
    private Integer accountId;
    private Integer settlementId;
    private Integer rewardId;
    private BigDecimal rewardAmount;
    private Boolean confirmed;
}
