package com.increff.pos.controller;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.DailySalesData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesData;
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
    @GetMapping(path = "/day-on-day")
    public List<DailySalesData> getDailySalesReport() {
        return reportDto.getDailySalesReport();
    }

    @ApiOperation(value = "Get sales reports")
    @PostMapping(path = "/sales")
    public List<SalesData> getSalesReport(@RequestBody SalesForm salesForm) throws ApiException {
        return reportDto.getSalesReport(salesForm);
    }

    @ApiOperation(value = "Get brand reports")
    @GetMapping(path = "/brand-report")
    public List<BrandData> getBrandReport() {
        return reportDto.getBrandCategoryReport();
    }

    @ApiOperation(value = "Get inventory reports")
    @GetMapping(path = "/inventory-report")
    public List<InventoryReportData> getInventoryReport() throws ApiException {
        return reportDto.getInventoryReport();
    }

    @ApiOperation(value = "insert daily sales report")
    @PostMapping(path = "/day-on-day")
    public void insertDailySaleReport(){
        reportDto.insertDailySalesReport();
    }

}
