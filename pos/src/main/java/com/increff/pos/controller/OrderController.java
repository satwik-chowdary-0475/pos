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
@RequestMapping(path = "/api/order")
public class OrderController {

    @Autowired
    private OrderDto orderDto;
    @Autowired
    private OrderItemDto orderItemDto;

    @ApiOperation(value = "Creates an order")
    @PostMapping(path = "")
    public String insert(@RequestBody OrderForm orderForm) throws ApiException {
        return orderDto.insert(orderForm);
    }

    @ApiOperation(value = "Get details of an order")
    @GetMapping(path = "/{orderCode}")
    public OrderDetailsData getOrder(@PathVariable String orderCode) throws ApiException {
        return orderDto.getAllDetails(orderCode);
    }

    @ApiOperation(value = "Get list of all orders")
    @GetMapping(path = "")
    public List<OrderData> getAllOrders() throws ApiException {
        return orderDto.getAllOrders();
    }

    @ApiOperation(value = "Delete an order")
    @DeleteMapping(path = "/{orderCode}")
    public void delete(@PathVariable String orderCode) throws ApiException {
        orderDto.delete(orderCode);
    }

    @ApiOperation(value = "Changes the order status to invoiced!!")
    @PutMapping(path = "/{orderCode}")
    public void changeStatus(@PathVariable String orderCode) throws ApiException {
        orderDto.changeStatus(orderCode);
    }


    @ApiOperation(value = "Add order item to the order")
    @PostMapping(path = "/{orderId}/order-items")
    public void insertOrderItem(@PathVariable int orderId, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        orderItemDto.insert(orderId, orderItemForm);
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
        orderItemDto.update(orderId, id, orderItemForm);
    }

    @ApiOperation(value = "Delete an order-item")
    @DeleteMapping(path = "/{orderId}/order-items/{id}")
    public void deleteOrderItem(@PathVariable int orderId, @PathVariable int id) throws ApiException {
        orderItemDto.delete(orderId, id);
    }


}
