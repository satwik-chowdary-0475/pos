package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class SalesForm {
    private String brand;
    private String category;
    private String startTime;
    private String endTime;
}
