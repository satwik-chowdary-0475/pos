package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
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
    public int insert(int orderId, OrderItemForm orderItemForm) throws ApiException {
        HelperDto.normalise(orderItemForm);
        ProductPojo productPojo = productService.select(orderItemForm.getBarcode());
        OrderPojo orderPojo = orderService.select(orderId);
        validateStatus(orderPojo.getStatus());
        OrderItemPojo orderItemPojo = HelperDto.convert(orderItemForm, orderId, productPojo);
        InventoryPojo inventoryPojo = inventoryService.select(orderItemPojo.getProductId());
        int requiredQuantity = orderItemPojo.getQuantity();
        int inventoryQuantity = inventoryPojo.getQuantity();
        inventoryService.update(inventoryPojo, inventoryQuantity - requiredQuantity);
        return orderItemService.insert(orderItemPojo, inventoryPojo);
    }


    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemData> getOrderItems(int orderId) throws ApiException {
        OrderPojo orderPojo = orderService.select(orderId);
        List<OrderItemPojo> orderItemPojoList = orderItemService.selectAll(orderId);
        List<OrderItemData> orderItemDataList = new ArrayList<OrderItemData>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            ProductPojo productPojo = productService.select(orderItemPojo.getProductId());
            orderItemDataList.add(HelperDto.convert(orderItemPojo, productPojo.getBarcode(), productPojo.getName()));
        }
        return orderItemDataList;
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemData getOrderItem(int orderId, int id) throws ApiException {
        OrderPojo orderPojo = orderService.select(orderId);
        OrderItemPojo orderItemPojo = orderItemService.select(orderId, id);
        ProductPojo productPojo = productService.select(orderItemPojo.getProductId());
        return HelperDto.convert(orderItemPojo, productPojo.getBarcode(), productPojo.getName());
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(int orderId, int id, OrderItemForm orderItemForm) throws ApiException {
        HelperDto.normalise(orderItemForm);
        ProductPojo productPojo = productService.select(orderItemForm.getBarcode());
        OrderPojo orderPojo = orderService.select(orderId);
        validateStatus(orderPojo.getStatus());
        OrderItemPojo orderItemPojo = HelperDto.convert(orderItemForm, orderId, productPojo);
        InventoryPojo inventoryPojo = inventoryService.select(productPojo.getId());
        OrderItemPojo existingOrderItemPojo = orderItemService.select(orderId, id);
        int requiredQuantity = orderItemPojo.getQuantity();
        int inventoryQuantity = inventoryPojo.getQuantity();
        int previousQuantity = existingOrderItemPojo.getQuantity();
        orderItemService.update(orderId, id, orderItemPojo, inventoryPojo);
        int updatedQuantity = inventoryQuantity + previousQuantity - requiredQuantity;
        inventoryService.update(inventoryPojo, updatedQuantity);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(int orderId, int id) throws ApiException {
        OrderPojo orderPojo = orderService.select(orderId);
        validateStatus(orderPojo.getStatus());
        OrderItemPojo orderItemPojo = orderItemService.select(orderId, id);
        InventoryPojo inventoryPojo = inventoryService.select(orderItemPojo.getProductId());
        orderItemService.delete(orderId, id);
        inventoryService.update(inventoryPojo, inventoryPojo.getQuantity() + orderItemPojo.getQuantity());
    }

    public void validateStatus(String orderStatus) throws ApiException {
        if (orderStatus.equals("INVOICED")) {
            throw new ApiException("Order cannot be modified!!");
        }
    }

}
