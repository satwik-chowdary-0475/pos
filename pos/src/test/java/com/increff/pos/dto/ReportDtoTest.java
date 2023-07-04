package com.increff.pos.dto;

import com.increff.pos.Helper;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.DailySalesData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesData;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import com.increff.pos.spring.AbstractUnitTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

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

    // TODO : TO TEST HELPERS AND NORMALISE CASES
    @Before
    public void init() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1", "category 1");
        brandDto.insertBrand(brandForm);
        BrandForm brandForm1 = Helper.createBrandForm("brand 2","category 1");
        brandDto.insertBrand(brandForm1);
        BrandForm brandForm2 = Helper.createBrandForm("brand 1","category 2");
        brandDto.insertBrand(brandForm2);
        ProductForm productForm = Helper.createProductForm("barcode 1", "brand 1", "category 1", "product 1", 120.12);
        productDto.insert(productForm);
        ProductForm productForm1 = Helper.createProductForm("barcode 2", "brand 1", "category 1", "product 2", 120.12);
        productDto.insert(productForm1);
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1", 200);
        inventoryDto.insert(inventoryForm);
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1", 10, 120.12);
        orderItemDto.insert(orderPojo.getId(), orderItemForm);
        orderDto.changeStatus(orderCode);
    }

    @Test
    public void TestBrandCategoryReport() throws ApiException{
        List<BrandData> brandDataList = reportDto.getBrandCategoryReport();
        assertEquals(brandDataList.size() , 3);
    }

    @Test
    public void TestInventoryReport() throws ApiException{
        List<InventoryReportData>inventoryReportDataList = reportDto.getInventoryReport();
        assertEquals(inventoryReportDataList.size(),3);
    }


    //TODO: Not working should look into this
//    @Ignore
    @Test
    public void TestDailyReport() throws ApiException{

        reportDto.insertDailyReport();
        List<DailySalesData>dailySalesData = reportDto.getDailySalesReport();
        assertEquals(dailySalesData.size(),1);
    }

    @Test
    public void TestGetSalesReport() throws ApiException{
        ZonedDateTime startTime = ZonedDateTime.parse("2022-06-22T10:15:30+01:00[Asia/Kolkata]");
        ZonedDateTime endTime = ZonedDateTime.parse("2022-06-30T10:15:30+01:00[Asia/Kolkata]");
        SalesForm salesForm = Helper.createSalesForm("brand 1","category 1",startTime,endTime);
        List<SalesData>salesDataList = reportDto.getSalesReport(salesForm);
        assertEquals(salesDataList.size(),1);
    }

    @Test
    public void TestGetSalesReportEmptyBrand() throws ApiException{
        ZonedDateTime startTime = ZonedDateTime.parse("2022-06-22T10:15:30+01:00[Asia/Kolkata]");
        ZonedDateTime endTime = ZonedDateTime.parse("2022-06-30T10:15:30+01:00[Asia/Kolkata]");
        SalesForm salesForm = Helper.createSalesForm("","category 1",startTime,endTime);
        List<SalesData>salesDataList = reportDto.getSalesReport(salesForm);
        assertEquals(salesDataList.size(),2);
    }

    @Test
    public void TestGetSalesReportEmptyCategory() throws ApiException{
        ZonedDateTime startTime = ZonedDateTime.parse("2022-06-22T10:15:30+01:00[Asia/Kolkata]");
        ZonedDateTime endTime = ZonedDateTime.parse("2022-06-30T10:15:30+01:00[Asia/Kolkata]");
        SalesForm salesForm = Helper.createSalesForm("brand 1","",startTime,endTime);
        List<SalesData>salesDataList = reportDto.getSalesReport(salesForm);
        assertEquals(salesDataList.size(),2);
    }

    @Test
    public void TestGetSalesReportBothEmpty() throws ApiException{
        ZonedDateTime startTime = ZonedDateTime.parse("2022-06-22T10:15:30+01:00[Asia/Kolkata]");
        ZonedDateTime endTime = ZonedDateTime.parse("2022-06-30T10:15:30+01:00[Asia/Kolkata]");
        SalesForm salesForm = Helper.createSalesForm("","",startTime,endTime);
        List<SalesData>salesDataList = reportDto.getSalesReport(salesForm);
        assertEquals(salesDataList.size(),3);
    }






}
