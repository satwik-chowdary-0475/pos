package com.increff.pos.dto.helper;

import com.increff.pos.model.data.DailySalesData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesData;
import com.increff.pos.model.form.SalesForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DailySalesReportPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.StringUtil;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportHelperDto {
    public static DailySalesData convert(DailySalesReportPojo dailySalesReportPojo) {
        DailySalesData dailySalesData = new DailySalesData();
        dailySalesData.setTotalRevenue(dailySalesReportPojo.getTotalRevenue());
        dailySalesData.setInvoicedItemsCount(dailySalesReportPojo.getInvoicedItemsCount());
        dailySalesData.setInvoicedOrdersCount(dailySalesReportPojo.getInvoicedOrdersCount());
        dailySalesData.setDate(dailySalesReportPojo.getTime());
        return dailySalesData;
    }

    public static SalesData convert(BrandPojo brandPojo,Integer quantity,Double totalRevenue){
        SalesData salesData = new SalesData();
        salesData.setQuantity(quantity);
        salesData.setCategory(brandPojo.getCategory());
        salesData.setBrand(brandPojo.getBrand());
        salesData.setRevenue(totalRevenue);
        return salesData;
    }

    public static void validate(SalesForm salesForm) throws ApiException {
        if(salesForm==null){
            throw new ApiException("Invalid sales form details");
        }
        if(salesForm.getBrand() == null){
            throw new ApiException("Invalid brand name");
        }
        if(salesForm.getCategory() == null){
            throw new ApiException("Invalid category name");
        }
    }
    public static void normalise(SalesForm salesForm) throws ApiException {
        salesForm.setBrand(StringUtil.toLowerCase(salesForm.getBrand()));
        salesForm.setCategory(StringUtil.toLowerCase(salesForm.getCategory()));
        ReportHelperDto.validate(salesForm);
    }
}
