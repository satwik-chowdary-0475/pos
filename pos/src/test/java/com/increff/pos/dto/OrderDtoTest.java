package com.increff.pos.dto;

import com.increff.pos.Helper;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderDetailsData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.spring.AbstractUnitTest;
import com.increff.pos.pojo.OrderStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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

    @Before
    public void init() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1","category 1");
        brandDto.insert(brandForm);
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        productDto.insert(productForm);
        ProductForm productForm1 = Helper.createProductForm("barcode 2","brand 1","category 1","product 2",120.12);
        productDto.insert(productForm1);
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",200);
        inventoryDto.insert(inventoryForm);
    }

    @Test
    public void TestInsert() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.getByOrderCode(orderCode);
        assertEquals(orderPojo.getOrderCode(),orderCode);
        assertEquals(orderPojo.getStatus(), OrderStatus.CREATED);
        assertEquals(orderPojo.getCustomerName(),"customer 1");
    }

    @Test
    public void TestChangeStatus() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        orderDto.changeStatus(orderCode);
        OrderPojo orderPojo = orderService.getByOrderCode(orderCode);
        assertEquals(orderPojo.getStatus(),OrderStatus.INVOICED);
    }

    @Test
    public void TestGetAllOrders() throws ApiException{
        List<OrderData> actualOrderDataList = new ArrayList<OrderData>();
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.getByOrderCode(orderCode);
        actualOrderDataList.add(Helper.createOrderData(orderPojo));
        OrderForm orderForm1 = Helper.createOrderForm("customer 2");
        String orderCode1 = orderDto.insert(orderForm1);
        OrderPojo orderPojo1 = orderService.getByOrderCode(orderCode1);
        actualOrderDataList.add(Helper.createOrderData(orderPojo1));
        PaginatedData actualPaginatedData = new PaginatedData(actualOrderDataList, orderService.getCount());
        PaginatedData expectedPaginatedData = orderDto.getAll(1,10);
        assertEquals(actualPaginatedData.getDataList().size(),expectedPaginatedData.getDataList().size());
    }

    @Test
    public void TestGetAllDetails() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.getByOrderCode(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",100,100.12);
        orderItemDto.insert(orderPojo.getId(),orderItemForm);
        ProductPojo productPojo = productService.getByBarcode("barcode 1");
        // Optional.ofNullable checks if the object is not null
        assertEquals(Optional.ofNullable((inventoryService.getByProductId(productPojo.getId()).getQuantity())),Optional.ofNullable(100));
        List<OrderItemData>orderItemDataList = orderItemDto.getAllByOrderId(orderPojo.getId());
        OrderDetailsData actualOrderDetailsData = Helper.createOrderDetailsData(orderPojo,orderItemDataList);
        OrderDetailsData expectedOrderDetailsData = orderDto.getAllOrderDetails(orderCode);
        assertEquals(actualOrderDetailsData.getStatus(),expectedOrderDetailsData.getStatus());
        assertEquals(actualOrderDetailsData.getOrderItems().size(),expectedOrderDetailsData.getOrderItems().size());
        assertEquals(actualOrderDetailsData.getCreatedAt(),expectedOrderDetailsData.getCreatedAt());
        assertEquals(actualOrderDetailsData.getInvoicedAt(),expectedOrderDetailsData.getInvoicedAt());
        assertEquals(actualOrderDetailsData.getId(),expectedOrderDetailsData.getId());
    }


    @Test
    public void TestStatusChange() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        orderDto.changeStatus(orderCode);
        OrderPojo orderPojo = orderService.getByOrderCode(orderCode);
        assertEquals(orderPojo.getStatus(),OrderStatus.INVOICED);
    }

    @Test
    public void TestDelete() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.getByOrderCode(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",100,100.12);
        orderItemDto.insert(orderPojo.getId(),orderItemForm);
        ProductPojo productPojo = productService.getByBarcode("barcode 1");
        assertEquals(Optional.ofNullable(inventoryService.getByProductId(productPojo.getId()).getQuantity()),Optional.ofNullable(100));
        //check order delete
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Order doesn't exist");
        orderDto.delete(orderCode);
        orderService.getByOrderCode(orderCode);

        //check order item delete
        List<OrderItemData>orderItemDataList = orderItemDto.getAllByOrderId(orderPojo.getId());
        assertEquals(orderItemDataList.size() , 0);

        //check inventory update
        assertEquals(Optional.ofNullable(inventoryService.getByProductId(productPojo.getId()).getQuantity()),Optional.ofNullable(200));
    }



}
