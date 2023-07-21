package com.increff.pos.dto;

import com.increff.pos.Helper;
import com.increff.pos.model.data.*;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import com.increff.pos.spring.AbstractUnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

public class ReportDtoTest extends AbstractUnitTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Autowired
    private BrandDto brandDto;
    @Autowired
    private ProductDto productDto;
    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private OrderDto orderDto;
    @Autowired
    private OrderItemDto orderItemDto;
    @Autowired
    private ReportDto reportDto;
    @Autowired
    private OrderService orderService;

    @Before
    public void init() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1", "category 1");
        brandDto.insert(brandForm);
        BrandForm brandForm1 = Helper.createBrandForm("brand 2", "category 1");
        brandDto.insert(brandForm1);
        BrandForm brandForm2 = Helper.createBrandForm("brand 1", "category 2");
        brandDto.insert(brandForm2);
        ProductForm productForm = Helper.createProductForm("barcode 1", "brand 1", "category 1", "product 1", 120.12);
        productDto.insert(productForm);
        ProductForm productForm1 = Helper.createProductForm("barcode 2", "brand 1", "category 1", "product 2", 120.12);
        productDto.insert(productForm1);
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1", 200);
        inventoryDto.insert(inventoryForm);
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.getByOrderCode(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1", 10, 120.12);
        orderItemDto.insert(orderPojo.getId(), orderItemForm);
        orderDto.changeStatus(orderCode);
        OrderForm orderForm1 = Helper.createOrderForm("customer 2");
        String orderCode1 = orderDto.insert(orderForm1);
        OrderPojo orderPojo1 = orderService.getByOrderCode(orderCode1);
        OrderItemForm orderItemForm1 = Helper.createOrderItemForm("barcode 1", 10, 120.12);
        orderItemDto.insert(orderPojo1.getId(), orderItemForm1);
        orderDto.changeStatus(orderCode1);
    }

    @Test
    public void TestBrandCategoryReport() throws ApiException {
        List<BrandReportData> brandDataList = reportDto.getBrandCategoryReport();
        assertEquals(brandDataList.size(), 3);
    }

    @Test
    public void TestInventoryReport() throws ApiException {
        List<InventoryReportData> inventoryReportDataList = reportDto.getInventoryReport();
        assertEquals(inventoryReportDataList.size(), 3);
        assertEquals(Optional.ofNullable(inventoryReportDataList.get(0).getQuantity()),Optional.ofNullable(180));
        assertEquals(Optional.ofNullable(inventoryReportDataList.get(1).getQuantity()),Optional.ofNullable(0));
        assertEquals(Optional.ofNullable(inventoryReportDataList.get(2).getQuantity()),Optional.ofNullable(0));
    }


    @Test
    public void TestDailyReport() throws ApiException {
        reportDto.insertDailySalesReport();
        List<DailySalesData> dailySalesData = reportDto.getDailySalesReport();
        assertEquals(dailySalesData.size(), 1);
    }

    @Test
    public void TestGetSalesReport() throws ApiException {
        Date startTime = Date.valueOf(LocalDate.now().minusDays(1));
        Date endTime = Date.valueOf(LocalDate.now());
        SalesForm salesForm = Helper.createSalesForm("brand 1", "category 1", startTime.toString(), endTime.toString());
        List<SalesData> salesDataList = reportDto.getSalesReport(salesForm);
        assertEquals(salesDataList.size(), 1);
        assertEquals(salesDataList.get(0).getRevenue(),2402.4);
        assertEquals(Optional.ofNullable(salesDataList.get(0).getQuantity()),Optional.ofNullable(20));
    }

    @Test
    public void TestGetSalesReportEmptyBrand() throws ApiException {
        Date startTime = Date.valueOf(LocalDate.now().minusDays(1));
        Date endTime = Date.valueOf(LocalDate.now());
        SalesForm salesForm = Helper.createSalesForm("", "category 1", startTime.toString(), endTime.toString());
        List<SalesData> salesDataList = reportDto.getSalesReport(salesForm);
        assertEquals(salesDataList.size(), 2);
        assertEquals(salesDataList.get(0).getRevenue(),2402.4);
        assertEquals(Optional.ofNullable(salesDataList.get(0).getQuantity()),Optional.ofNullable(20));
        assertEquals(salesDataList.get(1).getRevenue(),0.0);
        assertEquals(Optional.ofNullable(salesDataList.get(1).getQuantity()),Optional.ofNullable(0));
    }



    @Test
    public void TestGetSalesReportEmptyCategory() throws ApiException {
        Date startTime = Date.valueOf(LocalDate.now().minusDays(1));
        Date endTime = Date.valueOf(LocalDate.now());
        SalesForm salesForm = Helper.createSalesForm("brand 1", "", startTime.toString(), endTime.toString());
        List<SalesData> salesDataList = reportDto.getSalesReport(salesForm);
        assertEquals(salesDataList.size(), 2);
        assertEquals(salesDataList.get(0).getRevenue(),2402.4);
        assertEquals(Optional.ofNullable(salesDataList.get(0).getQuantity()),Optional.ofNullable(20));
        assertEquals(salesDataList.get(1).getRevenue(),0.0);
        assertEquals(Optional.ofNullable(salesDataList.get(1).getQuantity()),Optional.ofNullable(0));
    }

    @Test
    public void TestGetSalesReportBothEmpty() throws ApiException {
        Date startTime = Date.valueOf(LocalDate.now().minusDays(1));
        Date endTime = Date.valueOf(LocalDate.now());
        SalesForm salesForm = Helper.createSalesForm("", "", startTime.toString(), endTime.toString());
        List<SalesData> salesDataList = reportDto.getSalesReport(salesForm);
        assertEquals(salesDataList.size(), 3);
        assertEquals(salesDataList.get(0).getRevenue(),2402.4);
        assertEquals(Optional.ofNullable(salesDataList.get(0).getQuantity()),Optional.ofNullable(20));
        assertEquals(salesDataList.get(1).getRevenue(),0.0);
        assertEquals(Optional.ofNullable(salesDataList.get(1).getQuantity()),Optional.ofNullable(0));
        assertEquals(salesDataList.get(2).getRevenue(),0.0);
        assertEquals(Optional.ofNullable(salesDataList.get(2).getQuantity()),Optional.ofNullable(0));

    }


}
