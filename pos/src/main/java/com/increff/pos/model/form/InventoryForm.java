package com.increff.pos.model.form;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InventoryForm {
    private String barcode;
    private Integer quantity;
}
