package com.increff.pos.flow;

import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class OrderItemFlow {

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryService inventoryService;

    @Transactional(rollbackOn = ApiException.class)
    public int insert(OrderItemPojo orderItemPojo, OrderStatus orderStatus, InventoryPojo inventoryPojo, String barcode) throws ApiException {
        validateOrderStatus(orderStatus);

        int orderItemId = orderItemService.insert(orderItemPojo, inventoryPojo,barcode);
        updateInventory(inventoryPojo, orderItemPojo.getQuantity());

        return orderItemId;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(OrderPojo orderPojo, OrderItemPojo orderItemPojo, InventoryPojo inventoryPojo) throws ApiException{
        validateOrderStatus(orderPojo.getStatus());

        validateOrderItem(orderPojo,orderItemPojo);

        orderItemService.delete(orderItemPojo.getId());
        inventoryService.update(inventoryPojo, inventoryPojo.getQuantity() + orderItemPojo.getQuantity());
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(OrderPojo orderPojo,OrderItemPojo existingOrderItemPojo,OrderItemPojo orderItemPojo) throws ApiException{
        validateOrderStatus(orderPojo.getStatus());
        validateOrderItem(orderPojo,existingOrderItemPojo);

        ProductPojo productPojo = productService.getById(existingOrderItemPojo.getProductId());

        int requiredQuantity = orderItemPojo.getQuantity();
        int previousQuantity = existingOrderItemPojo.getQuantity();
        InventoryPojo inventoryPojo = inventoryService.getByProductId(existingOrderItemPojo.getProductId());

        orderItemService.update(existingOrderItemPojo.getId(), orderItemPojo, inventoryPojo, productPojo.getBarcode());
        updateInventory(inventoryPojo, (requiredQuantity - previousQuantity));
    }
    private void validateOrderItem(OrderPojo orderPojo, OrderItemPojo orderItemPojo) throws ApiException {
        if (!orderPojo.getId().equals(orderItemPojo.getOrderId())) {
            throw new ApiException("Order id provided does not match the order id associated with the order item");
        }
    }

    private void updateInventory(InventoryPojo inventoryPojo, int requiredQuantity) {
        int inventoryQuantity = inventoryPojo.getQuantity();
        inventoryService.update(inventoryPojo, inventoryQuantity - requiredQuantity);
    }

    private void validateOrderStatus(OrderStatus orderStatus) throws ApiException {
        if (orderStatus.equals(OrderStatus.INVOICED)) {
            throw new ApiException("Order cannot be modified");
        }
    }


}
