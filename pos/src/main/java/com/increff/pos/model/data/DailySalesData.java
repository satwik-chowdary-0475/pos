package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class DailySalesData {
    //TODO: String or zonedDatetime?? zonedDatetime returning epochs
    private String date;
    private Integer invoicedOrdersCount;
    private Integer invoicedItemsCount;
    private Double totalRevenue;
}
