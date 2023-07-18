package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemUpdateForm {
    private Double sellingPrice;
    private Integer quantity;
}
