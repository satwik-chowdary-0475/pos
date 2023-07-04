package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SalesData {
    private String brand;
    private String category;
    private Integer quantity;
    private Double revenue;
}
