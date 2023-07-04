package com.increff.pos.model.data;

import com.increff.pos.model.form.OrderForm;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class OrderData extends OrderForm {
    private Integer id;
    private String orderCode;
    private String status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
