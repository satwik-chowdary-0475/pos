package com.increff.pos.dto;

import com.increff.pos.Helper;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderDetailsData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.spring.AbstractUnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

public class OrderDtoTest extends AbstractUnitTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    @Autowired
    private BrandDto brandDto;
    @Autowired
    private OrderDto orderDto;
    @Autowired
    private ProductDto productDto;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private OrderItemDto orderItemDto;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryService inventoryService;

    //TODO: Better naming in tests

    @Before
    public void init() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1","category 1");
        brandDto.insertBrand(brandForm);
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        productDto.insert(productForm);
        ProductForm productForm1 = Helper.createProductForm("barcode 2","brand 1","category 1","product 2",120.12);
        productDto.insert(productForm1);
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",200);
        inventoryDto.insert(inventoryForm);
    }

    // TODO : TO TEST HELPERS AND NORMALISE CASES
    @Test
    public void TestInsert() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        assertEquals(orderPojo.getOrderCode(),orderCode);
        assertEquals(orderPojo.getStatus(),"ACTIVE");
        assertEquals(orderPojo.getCustomerName(),"customer 1");
    }

    @Test
    public void TestChangeStatus() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        orderDto.changeStatus(orderCode);
        OrderPojo orderPojo = orderService.select(orderCode);
        assertEquals(orderPojo.getStatus(),"INVOICED");
    }

    @Test
    public void TestGetAllOrders() throws ApiException{
        List<OrderData> actualOrderDataList = new ArrayList<OrderData>();
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        actualOrderDataList.add(Helper.createOrderData(orderPojo));
        OrderForm orderForm1 = Helper.createOrderForm("customer 2");
        String orderCode1 = orderDto.insert(orderForm1);
        OrderPojo orderPojo1 = orderService.select(orderCode1);
        actualOrderDataList.add(Helper.createOrderData(orderPojo1));
        List<OrderData> expectedDataList = orderDto.getAllOrders();
        assertEquals(expectedDataList.size(),actualOrderDataList.size());
    }

    @Test
    public void TestGetAllDetails() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",100,100.12);
        orderItemDto.insert(orderPojo.getId(),orderItemForm);
        ProductPojo productPojo = productService.select("barcode 1");
        // Optional.ofNullable checks if the object is not null
        assertEquals(Optional.ofNullable((inventoryService.select(productPojo.getId()).getQuantity())),Optional.ofNullable(100));
        List<OrderItemData>orderItemDataList = orderItemDto.getOrderItems(orderPojo.getId());
        OrderDetailsData actualOrderDetailsData = Helper.createOrderDetailsData(orderPojo,orderItemDataList);
        OrderDetailsData expectedOrderDetailsData = orderDto.getAllDetails(orderCode);
        assertEquals(actualOrderDetailsData.getStatus(),expectedOrderDetailsData.getStatus());
        assertEquals(actualOrderDetailsData.getOrderItems().length,expectedOrderDetailsData.getOrderItems().length);
        assertEquals(actualOrderDetailsData.getCreatedAt(),expectedOrderDetailsData.getCreatedAt());
        assertEquals(actualOrderDetailsData.getInvoicedAt(),expectedOrderDetailsData.getInvoicedAt());
        assertEquals(actualOrderDetailsData.getId(),expectedOrderDetailsData.getId());
    }


    @Test
    public void TestStatusChange() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        orderDto.changeStatus(orderCode);
        OrderPojo orderPojo = orderService.select(orderCode);
        assertEquals(orderPojo.getStatus(),"INVOICED");
    }

    @Test
    public void TestDelete() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",100,100.12);
        orderItemDto.insert(orderPojo.getId(),orderItemForm);
        ProductPojo productPojo = productService.select("barcode 1");
        assertEquals(Optional.ofNullable(inventoryService.select(productPojo.getId()).getQuantity()),Optional.ofNullable(100));
        //check order delete
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Order doesn't exist!!");
        orderDto.delete(orderCode);
        OrderPojo deletedOrderPojo = orderService.select(orderCode);

        //check order item delete
        List<OrderItemData>orderItemDataList = orderItemDto.getOrderItems(orderPojo.getId());
        assertEquals(orderItemDataList.size() , 0);

        //check inventory update
        assertEquals(Optional.ofNullable(inventoryService.select(productPojo.getId()).getQuantity()),Optional.ofNullable(200));
    }



}
