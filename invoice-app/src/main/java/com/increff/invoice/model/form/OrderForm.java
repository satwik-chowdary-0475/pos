package com.increff.invoice.model.form;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;


@Setter
@Getter
public class OrderForm {
    private String customerName;
    private String invoicedTime;
    private Integer orderId;
    private OrderItemForm[] orderItems;
}
