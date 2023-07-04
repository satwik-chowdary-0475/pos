package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemForm{
    private String barcode;
    private Integer quantity;
    private Double sellingPrice;
}
