package com.increff.pos.dto.helper;

import com.increff.pos.model.data.*;
import com.increff.pos.model.form.SalesForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DailySalesReportPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.StringUtil;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportHelperDto {

    public static BrandReportData convert(BrandPojo brandPojo) {
        BrandReportData brandReportData = new BrandReportData();
        brandReportData.setBrand(brandPojo.getBrand());
        brandReportData.setCategory(brandPojo.getCategory());
        return brandReportData;
    }

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

    public static ReportData convert(Integer quantity, Double revenue) {
        ReportData reportData = new ReportData();
        reportData.setRevenue(revenue);
        reportData.setQuantity(quantity);
        return reportData;
    }

    public static void validate(SalesForm salesForm) throws ApiException {
        if (Objects.isNull(salesForm)) {
            throw new ApiException("Invalid sales form details");
        }
        validateStringField(salesForm.getStartTime(), "Start date");
        validateStringField(salesForm.getEndTime(), "End date");

        validateDates(salesForm.getStartTime(), salesForm.getEndTime());
    }

    private static void validateStringField(String field, String fieldName) throws ApiException {
        if (Objects.isNull(field) || field.trim().isEmpty()) {
            throw new ApiException("Invalid " + fieldName.toLowerCase() + ". " + fieldName + " is required.");
        }
    }

    private static void validateDates(String startDateString, String endDateString) throws ApiException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateString, formatter);
        LocalDate endDate = LocalDate.parse(endDateString, formatter);
        LocalDate currentDate = LocalDate.now();
        if (startDate.isAfter(endDate)) {
            throw new ApiException("Start date cannot be greater than end date");
        }
        if (startDate.isAfter(currentDate) || endDate.isAfter(currentDate)) {
            throw new ApiException("Start date and end date cannot be greater than current date");
        }
    }

    public static void normalise(SalesForm salesForm) throws ApiException {
        salesForm.setBrand(StringUtil.toLowerCase(salesForm.getBrand()));
        salesForm.setCategory(StringUtil.toLowerCase(salesForm.getCategory()));
        ReportHelperDto.validate(salesForm);
    }

}
