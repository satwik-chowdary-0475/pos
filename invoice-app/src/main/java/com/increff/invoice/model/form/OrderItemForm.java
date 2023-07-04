package com.increff.invoice.model.form;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemForm{
    private Integer id;
    private String productName;
    private Integer quantity;
    private Double sellingPrice;
    private Double totalPrice;
    private String barcode;
}
