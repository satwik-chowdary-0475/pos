package com.increff.pos.service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Log4j
public class OrderItemService {

    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private InventoryService inventoryService;

    @Transactional(rollbackOn = ApiException.class)
    public int insertOrderItem(OrderItemPojo orderItemPojo, InventoryPojo inventoryPojo) throws ApiException {
        int requiredQuantity = orderItemPojo.getQuantity();
        int inventoryQuantity = inventoryPojo.getQuantity();
        checkInventory(requiredQuantity, inventoryQuantity);
        OrderItemPojo existingOrderItemPojo = orderItemDao.getOrderItemByProductId(orderItemPojo.getOrderId(), orderItemPojo.getProductId());
        if (Objects.nonNull(existingOrderItemPojo)) {
            return insertExistingOrderItem(existingOrderItemPojo, orderItemPojo);
        } else {
            return insertNewOrderItem(orderItemPojo);
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemPojo getOrderItemById(int orderId, int id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemDao.getOrderItemById(orderId, id);
        if (Objects.isNull(orderItemPojo)) {
            throw new ApiException("Order item with given order id and id doesn't exist!!");
        }
        return orderItemPojo;
    }


    @Transactional
    public List<OrderItemPojo> getAllOrderItems(int orderId) {
        return orderItemDao.getAllOrderItems(orderId);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateOrderItem(int orderId, int id, OrderItemPojo updatedOrderItemPojo, InventoryPojo inventoryPojo) throws ApiException {
        OrderItemPojo existingOrderItemPojo = orderItemDao.getOrderItemById(orderId, id);
        int requiredQuantity = updatedOrderItemPojo.getQuantity();
        int inventoryQuantity = inventoryPojo.getQuantity() + existingOrderItemPojo.getQuantity();
        checkInventory(requiredQuantity, inventoryQuantity);
        existingOrderItemPojo.setQuantity(requiredQuantity);
        existingOrderItemPojo.setSellingPrice(updatedOrderItemPojo.getSellingPrice());
    }

    @Transactional(rollbackOn = ApiException.class)
    public void deleteOrderItem(int orderId, int id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemDao.getOrderItemById(orderId, id);
        if (Objects.isNull(orderItemPojo)) {
            throw new ApiException("Order item doesn't exist!!");
        }
        orderItemDao.deleteOrderItem(orderId, id);
    }

    @Transactional
    public void deleteOrderItemsByOrder(int orderId) {
        orderItemDao.deleteAllOrderItems(orderId);
    }

    @Transactional
    private void checkInventory(int requiredQuantity, int inventoryQuantity) throws ApiException {
        if (requiredQuantity > inventoryQuantity) {
            throw new ApiException("Insufficient inventory for the product!!!");
        }
    }

    @Transactional
    private Integer insertNewOrderItem(OrderItemPojo orderItemPojo) {
        orderItemDao.insertOrderItem(orderItemPojo);
        return orderItemPojo.getId();
    }

    @Transactional
    private Integer insertExistingOrderItem(OrderItemPojo existingOrderItemPojo, OrderItemPojo orderItemPojo) {
        existingOrderItemPojo.setSellingPrice(orderItemPojo.getSellingPrice());
        existingOrderItemPojo.setQuantity(orderItemPojo.getQuantity() + existingOrderItemPojo.getQuantity());
        return existingOrderItemPojo.getId();
    }

}
