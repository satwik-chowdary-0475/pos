package com.increff.pos.controller;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.dto.OrderItemDto;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderDetailsData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/orders")
public class OrderController {

    @Autowired
    private OrderDto orderDto;
    @Autowired
    private OrderItemDto orderItemDto;

    @ApiOperation(value = "Creates an order")
    @PostMapping(path = "")
    public String createOrder(@RequestBody OrderForm orderForm) throws ApiException {
        return orderDto.createOrder(orderForm);
    }

    @ApiOperation(value = "Get details of an order")
    @GetMapping(path = "/{orderCode}")
    public OrderDetailsData getAllOrderDetails(@PathVariable String orderCode) throws ApiException {
        return orderDto.getAllOrderDetails(orderCode);
    }

    @ApiOperation(value = "Get list of all orders")
    @GetMapping(path = "")
    public List<OrderData> getAllOrders() throws ApiException {
        return orderDto.getAllOrders();
    }

    @ApiOperation(value = "Delete an order")
    @DeleteMapping(path = "/{orderCode}")
    public void deleteOrder(@PathVariable String orderCode) throws ApiException {
        orderDto.deleteOrder(orderCode);
    }

    @ApiOperation(value = "Changes the order status to invoiced!!")
    @PutMapping(path = "/{orderCode}")
    public void changeOrderStatus(@PathVariable String orderCode) throws ApiException {
        orderDto.changeOrderStatus(orderCode);
    }


    @ApiOperation(value = "Add order item to the order")
    @PostMapping(path = "/{orderId}/order-items")
    public void insertOrderItem(@PathVariable int orderId, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        orderItemDto.insertOrderItem(orderId, orderItemForm);
    }

    @ApiOperation(value = "Get all order items of a order")
    @GetMapping(path = "/{orderId}/order-items")
    public List<OrderItemData> getOrderItems(@PathVariable int orderId) throws ApiException {
        return orderItemDto.getOrderItems(orderId);
    }

    @ApiOperation(value = "Get an order-item")
    @GetMapping(path = "/{orderId}/order-items/{id}")
    public OrderItemData getOrderItem(@PathVariable int orderId, @PathVariable int id) throws ApiException {
        return orderItemDto.getOrderItem(orderId, id);
    }

    @ApiOperation(value = "Update an order-item")
    @PutMapping(path = "/{orderId}/order-items/{id}")
    public void updateOrderItem(@PathVariable int orderId, @PathVariable int id, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        orderItemDto.updateOrderItem(orderId, id, orderItemForm);
    }

    @ApiOperation(value = "Delete an order-item")
    @DeleteMapping(path = "/{orderId}/order-items/{id}")
    public void deleteOrderItem(@PathVariable int orderId, @PathVariable int id) throws ApiException {
        orderItemDto.deleteOrderItem(orderId, id);
    }


}
