package com.increff.pos.dto;

import com.increff.pos.Helper;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import com.increff.pos.spring.AbstractUnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class OrderItemDtoTest extends AbstractUnitTest {

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
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;

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

    @Test
    public void TestInsert() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
        OrderItemPojo orderItemPojo = orderItemService.select(orderPojo.getId(),id);
        ProductPojo productPojo = productService.select("barcode 1");
        // Check insert order item
        assertEquals(orderItemPojo.getOrderId(),orderPojo.getId());
        assertEquals(orderItemPojo.getProductId(),productPojo.getId());
        assertEquals(orderItemPojo.getSellingPrice(),120.12);
        assertEquals(Optional.ofNullable(orderItemPojo.getQuantity()),Optional.ofNullable(10));
        assertEquals(orderItemPojo.getOrderId(),orderPojo.getId());

        // Check inventory
        assertEquals(Optional.ofNullable(inventoryService.select(orderItemPojo.getProductId()).getQuantity()),Optional.ofNullable(190));
    }

    @Test
    public void TestInsertNotExistsProduct() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 3",10,120.12);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with given barcode doesn't exist!!");
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
    }

    @Test
    public void TestInsertNotExistsInventory() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 2",10,120.12);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with id not present in inventory!!");
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
    }

    @Test
    public void TestInsertInsufficientInventory() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",300,120.12);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Insufficient inventory for the product!!!");
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
    }

    @Test
    public void TestInsertAddExistingItem() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        orderItemDto.insert(orderPojo.getId(),orderItemForm);
        OrderItemForm newOrderItemForm = Helper.createOrderItemForm("barcode 1",20,12.23);
        int id = orderItemDto.insert(orderPojo.getId(),newOrderItemForm);
        OrderItemPojo orderItemPojo = orderItemService.select(orderPojo.getId(),id);

        //Check insert
        assertEquals(orderItemPojo.getSellingPrice(),12.23);
        assertEquals(Optional.ofNullable(orderItemPojo.getQuantity()),Optional.ofNullable(30));

        // Check inventory reduce
        assertEquals(Optional.ofNullable(inventoryService.select(orderItemPojo.getProductId()).getQuantity()),Optional.ofNullable(170));
    }

    @Test
    public void TestInsertInvoiced() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        orderDto.changeStatus(orderCode);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Order cannot be modified!!");
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        orderItemDto.insert(orderPojo.getId(),orderItemForm);
    }

    @Test
    public void TestDeleteOrderItem() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
        OrderItemPojo orderItemPojo = orderItemService.select(orderPojo.getId(),id);
        assertNotNull(orderItemPojo);
        assertEquals(Optional.ofNullable(inventoryService.select(orderItemPojo.getProductId()).getQuantity()), Optional.ofNullable(190));
        //Check delete
        orderItemDto.delete(orderPojo.getId(),id);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Order item with given order id and id doesn't exist!!");
        OrderItemPojo checkOrderItemPojo = orderItemService.select(orderPojo.getId(),id);

        // Check updated Inventory
        assertEquals(Optional.ofNullable(inventoryService.select(orderItemPojo.getProductId()).getQuantity()), Optional.ofNullable(200));
    }

    @Test
    public void TestDeleteNotExistsOrder() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Order doesn't exist!!");
        orderItemDto.delete(orderPojo.getId()+1,id);

    }

    @Test
    public void TestDeleteNotExistsOrderItem() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Order item with given order id and id doesn't exist!!");
        orderItemDto.delete(orderPojo.getId(),id+1);
    }

    @Test
    public void TestGetOrderItem() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
        OrderItemData orderItemData = orderItemDto.getOrderItem(orderPojo.getId(),id);
        assertEquals(orderItemData.getOrderId(),orderPojo.getId());
        assertEquals(Optional.ofNullable(orderItemData.getQuantity()),Optional.ofNullable(10));
        assertEquals(orderItemData.getBarcode(),"barcode 1");
        assertEquals(orderItemData.getProductName(),"product 1");
        assertEquals(orderItemData.getTotalPrice(),1201.20);
        assertEquals(orderItemData.getSellingPrice(),120.12);
    }

    @Test
    public void TestGetOrderItemNotExistsOrder() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Order doesn't exist!!");
        orderItemDto.getOrderItem(orderPojo.getId()+1,id);
    }

    @Test
    public void TestGetOrderItemNotExists() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Order item with given order id and id doesn't exist!!");
        orderItemDto.getOrderItem(orderPojo.getId(),id+1);
    }

    @Test
    public void TestUpdate() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
        OrderItemForm updatedOrderItemForm = Helper.createOrderItemForm("barcode 1",20,100.23);
        orderItemDto.update(orderPojo.getId(),id,updatedOrderItemForm);
        OrderItemPojo orderItemPojo = orderItemService.select(orderPojo.getId(),id);
        ProductPojo productPojo = productService.select("barcode 1");

        //Check for order item update
        assertEquals(orderItemPojo.getProductId(),productPojo.getId());
        assertEquals(orderItemPojo.getOrderId(),orderPojo.getId());
        assertEquals(Optional.ofNullable(orderItemPojo.getQuantity()),Optional.ofNullable(20));
        assertEquals(orderItemPojo.getSellingPrice(),100.23);

        //Check for inventory change
        InventoryPojo inventoryPojo = inventoryService.select(productPojo.getId());
        assertEquals(Optional.ofNullable(inventoryPojo.getQuantity()),Optional.ofNullable(180));
    }

    @Test
    public void TestUpdateNotExistProduct() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with given barcode doesn't exist!!");
        OrderItemForm updatedOrderItemForm = Helper.createOrderItemForm("barcode 3",20,100.23);
        orderItemDto.update(orderPojo.getId(),id,updatedOrderItemForm);
    }

    @Test
    public void TestUpdateNotExistOrder() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Order doesn't exist!!");
        OrderItemForm updatedOrderItemForm = Helper.createOrderItemForm("barcode 1",20,100.23);
        orderItemDto.update(orderPojo.getId()+1,id,updatedOrderItemForm);
    }

    @Test
    public void TestUpdateNotExistInventory() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Insufficient inventory for the product!!!");
        OrderItemForm updatedOrderItemForm = Helper.createOrderItemForm("barcode 1",220,100.23);
        orderItemDto.update(orderPojo.getId(),id,updatedOrderItemForm);
    }

    @Test
    public void TestUpdateNotExistOrderItem() throws ApiException{
        OrderForm orderForm = Helper.createOrderForm("customer 1");
        String orderCode = orderDto.insert(orderForm);
        OrderPojo orderPojo = orderService.select(orderCode);
        OrderItemForm orderItemForm = Helper.createOrderItemForm("barcode 1",10,120.12);
        int id = orderItemDto.insert(orderPojo.getId(),orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Order item with given order id and id doesn't exist!!");
        OrderItemForm updatedOrderItemForm = Helper.createOrderItemForm("barcode 1",20,100.23);
        orderItemDto.update(orderPojo.getId(),id+1,updatedOrderItemForm);
    }




}
