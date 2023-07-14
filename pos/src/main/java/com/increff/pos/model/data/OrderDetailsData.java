package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Setter
@Getter
public class OrderDetailsData {

    private Integer id;
    private String status;
    private ZonedDateTime invoicedAt;
    private ZonedDateTime createdAt;
    private String customerName;
    private String orderCode;
    private List<OrderItemData> orderItems;

}
