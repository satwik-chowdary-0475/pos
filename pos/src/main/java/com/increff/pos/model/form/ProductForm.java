package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductForm {
    private String barcode;
    private String brand;
    private String category;
    private String name;
    private Double mrp;
 }
