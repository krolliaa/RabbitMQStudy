package com.food.orderservicemanager.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateVO {
    private Integer accountId;
    private String address;
    private Integer productId;
}
