package com.increff.pos.controller;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.data.*;
import com.increff.pos.model.form.SalesForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    private BrandDto brandDto;

    @Autowired
    private ReportDto reportDto;

    @ApiOperation(value = "Get daily sales reports")
    @GetMapping(path = "/daily_sales")
    public List<DailySalesData> getDailySalesReport() {
        return reportDto.getDailySalesReport();
    }

    @ApiOperation(value = "Get sales reports")
    @PostMapping(path = "/sales")
    public List<SalesData> getSalesReport(@RequestBody SalesForm salesForm) throws ApiException {
        return reportDto.getSalesReport(salesForm);
    }

    @ApiOperation(value = "Get brand reports")
    @GetMapping(path = "/brands")
    public List<BrandReportData> getBrandReport() {
        return reportDto.getBrandCategoryReport();
    }

    @ApiOperation(value = "Get inventory reports")
    @GetMapping(path = "/inventory")
    public List<InventoryReportData> getInventoryReport() throws ApiException {
        return reportDto.getInventoryReport();
    }
    //TODO: should remove before presentation
    @ApiOperation(value = "insert daily sales report")
    @PostMapping(path = "/daily_sales")
    public void insertDailySaleReport() {
        reportDto.insertDailySalesReport();
    }

}
