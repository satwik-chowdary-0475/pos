package com.increff.pos.dto.helper;

import com.increff.pos.model.data.DailySalesData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.ReportData;
import com.increff.pos.model.data.SalesData;
import com.increff.pos.model.form.SalesForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DailySalesReportPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.StringUtil;
import javafx.util.Pair;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportHelperDto {

    public static InventoryReportData convert(String brand, String category, Integer quantity) {
        InventoryReportData inventoryReportData = new InventoryReportData();
        inventoryReportData.setBrand(brand);
        inventoryReportData.setCategory(category);
        inventoryReportData.setQuantity(quantity);
        return inventoryReportData;
    }

    public static DailySalesData convert(DailySalesReportPojo dailySalesReportPojo) {
        DailySalesData dailySalesData = new DailySalesData();
        dailySalesData.setTotalRevenue(dailySalesReportPojo.getTotalRevenue());
        dailySalesData.setInvoicedItemsCount(dailySalesReportPojo.getInvoicedItemsCount());
        dailySalesData.setInvoicedOrdersCount(dailySalesReportPojo.getInvoicedOrdersCount());
        ZonedDateTime zonedDateTime = dailySalesReportPojo.getTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        dailySalesData.setDate(zonedDateTime.format(formatter));
        return dailySalesData;
    }

    public static SalesData convert(BrandPojo brandPojo, Integer quantity, Double totalRevenue) {
        SalesData salesData = new SalesData();
        salesData.setQuantity(quantity);
        salesData.setCategory(brandPojo.getCategory());
        salesData.setBrand(brandPojo.getBrand());
        salesData.setRevenue(totalRevenue);
        return salesData;
    }

    public static ReportData convert(Integer quantity, Double revenue){
        ReportData reportData = new ReportData();
        reportData.setRevenue(revenue);
        reportData.setQuantity(quantity);
        return  reportData;
    }

    public static void validate(SalesForm salesForm) throws ApiException {
        if (Objects.isNull(salesForm)) {
            throw new ApiException("Invalid sales form details");
        }
        if(Objects.isNull(salesForm.getStartTime()) || salesForm.getStartTime().length() == 0){
            throw new ApiException("Invalid start time");
        }
        if(Objects.isNull(salesForm.getEndTime()) || salesForm.getEndTime().length() == 0){
            throw new ApiException("Invalid end time");
        }
        if(Objects.isNull(salesForm.getBrand()))
        {
            throw new ApiException("Invalid brand name");
        }
        if (Objects.isNull(salesForm.getCategory())) {
            throw new ApiException("Invalid category name");
        }
    }

    public static void normalise(SalesForm salesForm) throws ApiException {
        salesForm.setBrand(StringUtil.toLowerCase(salesForm.getBrand()));
        salesForm.setCategory(StringUtil.toLowerCase(salesForm.getCategory()));
        ReportHelperDto.validate(salesForm);
    }
}
