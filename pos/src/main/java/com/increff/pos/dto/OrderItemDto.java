package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import com.increff.pos.pojo.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderItemDto {

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;

    @Transactional(rollbackOn = ApiException.class)
    public int insertOrderItem(int orderId, OrderItemForm orderItemForm) throws ApiException {
        HelperDto.normalise(orderItemForm);
        ProductPojo productPojo = productService.getProductByBarcode(orderItemForm.getBarcode());
        OrderPojo orderPojo = orderService.getOrderByOrderId(orderId);
        validateStatus(orderPojo.getStatus());
        OrderItemPojo orderItemPojo = HelperDto.convert(orderItemForm, orderId, productPojo);
        InventoryPojo inventoryPojo = inventoryService.getProductInventoryByProductId(orderItemPojo.getProductId());
        int orderItemId = orderItemService.insertOrderItem(orderItemPojo, inventoryPojo);
        updateInventoryQuantity(inventoryPojo, orderItemPojo.getQuantity());
        return orderItemId;
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemData> getOrderItems(int orderId) throws ApiException {
        OrderPojo orderPojo = orderService.getOrderByOrderId(orderId);
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAllOrderItems(orderId);
        List<OrderItemData> orderItemDataList = new ArrayList<OrderItemData>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            ProductPojo productPojo = productService.getProductById(orderItemPojo.getProductId());
            orderItemDataList.add(HelperDto.convert(orderItemPojo, productPojo.getBarcode(), productPojo.getName()));
        }
        return orderItemDataList;
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemData getOrderItem(int orderId, int id) throws ApiException {
        OrderPojo orderPojo = orderService.getOrderByOrderId(orderId);
        OrderItemPojo orderItemPojo = orderItemService.getOrderItemById(id);//change
        ProductPojo productPojo = productService.getProductById(orderItemPojo.getProductId());
        return HelperDto.convert(orderItemPojo, productPojo.getBarcode(), productPojo.getName());
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateOrderItem(int orderId, int id, OrderItemForm orderItemForm) throws ApiException {
        HelperDto.normalise(orderItemForm);
        ProductPojo productPojo = productService.getProductByBarcode(orderItemForm.getBarcode());
        OrderPojo orderPojo = orderService.getOrderByOrderId(orderId);
        validateStatus(orderPojo.getStatus());
        OrderItemPojo orderItemPojo = HelperDto.convert(orderItemForm, orderId, productPojo);
        InventoryPojo inventoryPojo = inventoryService.getProductInventoryByProductId(productPojo.getId());
        OrderItemPojo existingOrderItemPojo = orderItemService.getOrderItemById(id);
        int requiredQuantity = orderItemPojo.getQuantity();
        int previousQuantity = existingOrderItemPojo.getQuantity();
        orderItemService.updateOrderItem(id, orderItemPojo, inventoryPojo);
        updateInventoryQuantity(inventoryPojo, (requiredQuantity - previousQuantity));
    }

    @Transactional(rollbackOn = ApiException.class)
    public void deleteOrderItem(int orderId, int id) throws ApiException {
        OrderPojo orderPojo = orderService.getOrderByOrderId(orderId);
        validateStatus(orderPojo.getStatus());
        OrderItemPojo orderItemPojo = orderItemService.getOrderItemById(id);
        InventoryPojo inventoryPojo = inventoryService.getProductInventoryByProductId(orderItemPojo.getProductId());
        orderItemService.deleteOrderItem(id);
        inventoryService.updateProductInInventory(inventoryPojo, inventoryPojo.getQuantity() + orderItemPojo.getQuantity());
    }

    private void validateStatus(OrderStatus orderStatus) throws ApiException {
        if (orderStatus.equals(OrderStatus.INVOICED)) {
            throw new ApiException("Order cannot be modified!!");
        }
    }

    private void updateInventoryQuantity(InventoryPojo inventoryPojo, int requiredQuantity) {
        int inventoryQuantity = inventoryPojo.getQuantity();
        inventoryService.updateProductInInventory(inventoryPojo, inventoryQuantity - requiredQuantity);
    }

}
