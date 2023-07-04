package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class SalesForm {

    private String brand;
    private String category;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;

}
