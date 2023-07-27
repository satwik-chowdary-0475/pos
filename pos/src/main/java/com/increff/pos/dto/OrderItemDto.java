package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.flow.OrderItemFlow;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.OrderItemUpdateForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderItemDto {

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemFlow orderItemFlow;

    public int insert(int orderId, OrderItemForm orderItemForm) throws ApiException {
        HelperDto.normalise(orderItemForm);

        ProductPojo productPojo = productService.getByBarcode(orderItemForm.getBarcode());
        OrderPojo orderPojo = orderService.getByOrderId(orderId);

        OrderItemPojo orderItemPojo = HelperDto.convert(orderItemForm, orderId, productPojo);
        InventoryPojo inventoryPojo = inventoryService.getByProductId(productPojo.getId());

        return orderItemFlow.insert(orderItemPojo,orderPojo.getStatus(),inventoryPojo,productPojo.getBarcode());

    }

    public List<OrderItemData> getAllByOrderId(int orderId) throws ApiException {
        orderService.getByOrderId(orderId);
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAllByOrderId(orderId);

        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            ProductPojo productPojo = productService.getById(orderItemPojo.getProductId());
            orderItemDataList.add(HelperDto.convert(orderItemPojo, productPojo.getBarcode(), productPojo.getName()));
        }

        return orderItemDataList;
    }

    public OrderItemData getById(int orderId, int id) throws ApiException {
        orderService.getByOrderId(orderId);
        OrderItemPojo orderItemPojo = orderItemService.getById(id);
        ProductPojo productPojo = productService.getById(orderItemPojo.getProductId());

        return HelperDto.convert(orderItemPojo, productPojo.getBarcode(), productPojo.getName());
    }

    public void update(int orderId, int id, OrderItemUpdateForm orderItemUpdateForm) throws ApiException {
        HelperDto.normalise(orderItemUpdateForm);

        OrderPojo orderPojo = orderService.getByOrderId(orderId);

        OrderItemPojo existingOrderItemPojo = orderItemService.getById(id);
        OrderItemPojo orderItemPojo = HelperDto.convert(orderItemUpdateForm, orderId);

        orderItemFlow.update(orderPojo,existingOrderItemPojo,orderItemPojo);
    }

    public void delete(int orderId, int id) throws ApiException {
        OrderPojo orderPojo = orderService.getByOrderId(orderId);
        OrderItemPojo orderItemPojo = orderItemService.getById(id);
        InventoryPojo inventoryPojo = inventoryService.getByProductId(orderItemPojo.getProductId());

        orderItemFlow.delete(orderPojo , orderItemPojo,inventoryPojo);
    }


}
