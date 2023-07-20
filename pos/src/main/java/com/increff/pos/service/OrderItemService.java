package com.increff.pos.service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j
public class OrderItemService {

    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private InventoryService inventoryService;

    @Transactional(rollbackOn = ApiException.class)
    public int insert(OrderItemPojo orderItemPojo, InventoryPojo inventoryPojo, String barcode) throws ApiException {
        int requiredQuantity = orderItemPojo.getQuantity();
        int inventoryQuantity = inventoryPojo.getQuantity();
        checkInventory(requiredQuantity, inventoryQuantity, barcode);

        OrderItemPojo existingOrderItemPojo = orderItemDao.getByProductId(orderItemPojo.getOrderId(), orderItemPojo.getProductId());
        if (Objects.nonNull(existingOrderItemPojo)) {
            return update(existingOrderItemPojo, orderItemPojo);
        }
        return insert(orderItemPojo);
    }

    @Transactional
    private Integer insert(OrderItemPojo orderItemPojo) {
        orderItemDao.insert(orderItemPojo);
        return orderItemPojo.getId();
    }

    private Integer update(OrderItemPojo existingOrderItemPojo, OrderItemPojo orderItemPojo) {
        existingOrderItemPojo.setSellingPrice(orderItemPojo.getSellingPrice());
        existingOrderItemPojo.setQuantity(orderItemPojo.getQuantity() + existingOrderItemPojo.getQuantity());
        return existingOrderItemPojo.getId();
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemPojo getById(int id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemDao.getById(id);
        if (Objects.isNull(orderItemPojo)) {
            throw new ApiException("Order item doesn't exist");
        }
        return orderItemPojo;
    }

    @Transactional
    public List<OrderItemPojo> getAllByOrderId(int orderId) {
        return orderItemDao.getAllByOrderId(orderId);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(int id, OrderItemPojo updatedOrderItemPojo, InventoryPojo inventoryPojo, String barcode) throws ApiException {
        OrderItemPojo existingOrderItemPojo = orderItemDao.getById(id);
        int requiredQuantity = updatedOrderItemPojo.getQuantity();
        int inventoryQuantity = inventoryPojo.getQuantity() + existingOrderItemPojo.getQuantity();
        checkInventory(requiredQuantity, inventoryQuantity, barcode);

        existingOrderItemPojo.setQuantity(requiredQuantity);
        existingOrderItemPojo.setSellingPrice(updatedOrderItemPojo.getSellingPrice());
    }

    //TODO: think about orders

    @Transactional(rollbackOn = ApiException.class)
    public void delete(int id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemDao.getById(id);
        if (Objects.isNull(orderItemPojo)) {
            throw new ApiException("Order item doesn't exist");
        }
        orderItemDao.delete(id);
    }

    @Transactional
    public void deleteByOrderId(int orderId) {
        orderItemDao.deleteByOrderId(orderId);
    }

    @Transactional
    public List<OrderItemPojo> getAllOrderItemsByOrderList(List<OrderPojo> orderPojoList) {
        if (!orderPojoList.isEmpty()) {
            List<Integer> orderIdsList = orderPojoList.stream().map(OrderPojo::getId).collect(Collectors.toList());
            return orderItemDao.getAllByOrderList(orderIdsList);
        }
        return new ArrayList<>();
    }

    private void checkInventory(int requiredQuantity, int inventoryQuantity, String barcode) throws ApiException {
        if (requiredQuantity > inventoryQuantity) {
            throw new ApiException("Insufficient inventory for the product with barcode " + barcode);
        }
    }
}
