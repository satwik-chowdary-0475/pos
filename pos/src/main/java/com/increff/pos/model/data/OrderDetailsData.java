package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
public class OrderDetailsData {

    private Integer id;
    private String status;
    private ZonedDateTime invoicedAt;
    private ZonedDateTime createdAt;
    private String customerName;
    private OrderItemData[] orderItems;

}
