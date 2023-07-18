package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.OrderItemUpdateForm;
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
    public int insert(int orderId, OrderItemForm orderItemForm) throws ApiException {
        HelperDto.normalise(orderItemForm);

        ProductPojo productPojo = productService.getProductByBarcode(orderItemForm.getBarcode());
        OrderPojo orderPojo = orderService.getByOrderId(orderId);
        validateStatus(orderPojo.getStatus());

        OrderItemPojo orderItemPojo = HelperDto.convert(orderItemForm, orderId, productPojo);
        InventoryPojo inventoryPojo = inventoryService.getById(orderItemPojo.getProductId());

        int orderItemId = orderItemService.insert(orderItemPojo, inventoryPojo, productPojo.getBarcode());
        updateInventory(inventoryPojo, orderItemPojo.getQuantity());
        return orderItemId;
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemData> getAllByOrderId(int orderId) throws ApiException {
        orderService.getByOrderId(orderId);
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAllByOrderId(orderId);

        List<OrderItemData> orderItemDataList = new ArrayList<OrderItemData>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            ProductPojo productPojo = productService.getProductById(orderItemPojo.getProductId());
            orderItemDataList.add(HelperDto.convert(orderItemPojo, productPojo.getBarcode(), productPojo.getName()));
        }

        return orderItemDataList;
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemData getById(int orderId, int id) throws ApiException {
        orderService.getByOrderId(orderId);
        OrderItemPojo orderItemPojo = orderItemService.getById(id);
        ProductPojo productPojo = productService.getProductById(orderItemPojo.getProductId());
        return HelperDto.convert(orderItemPojo, productPojo.getBarcode(), productPojo.getName());
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(int orderId, int id, OrderItemUpdateForm orderItemUpdateForm) throws ApiException {
        HelperDto.normalise(orderItemUpdateForm);

        OrderPojo orderPojo = orderService.getByOrderId(orderId);
        validateStatus(orderPojo.getStatus());

        OrderItemPojo existingOrderItemPojo = orderItemService.getById(id);
        ProductPojo productPojo = productService.getProductById(existingOrderItemPojo.getProductId());
        OrderItemPojo orderItemPojo = HelperDto.convert(orderItemUpdateForm, orderId);

        int requiredQuantity = orderItemPojo.getQuantity();
        int previousQuantity = existingOrderItemPojo.getQuantity();
        InventoryPojo inventoryPojo = inventoryService.getById(existingOrderItemPojo.getProductId());

        orderItemService.update(id, orderItemPojo, inventoryPojo, productPojo.getBarcode());
        updateInventory(inventoryPojo, (requiredQuantity - previousQuantity));
    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(int orderId, int id) throws ApiException {
        OrderPojo orderPojo = orderService.getByOrderId(orderId);
        validateStatus(orderPojo.getStatus());

        OrderItemPojo orderItemPojo = orderItemService.getById(id);
        InventoryPojo inventoryPojo = inventoryService.getById(orderItemPojo.getProductId());

        orderItemService.delete(orderId, id);
        inventoryService.update(inventoryPojo, inventoryPojo.getQuantity() + orderItemPojo.getQuantity());
    }

    private void validateStatus(OrderStatus orderStatus) throws ApiException {
        if (orderStatus.equals(OrderStatus.INVOICED)) {
            throw new ApiException("Order cannot be modified");
        }
    }

    private void updateInventory(InventoryPojo inventoryPojo, int requiredQuantity) {
        int inventoryQuantity = inventoryPojo.getQuantity();
        inventoryService.update(inventoryPojo, inventoryQuantity - requiredQuantity);
    }

}
