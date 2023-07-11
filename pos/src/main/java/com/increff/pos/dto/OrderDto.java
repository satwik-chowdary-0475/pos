package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderDetailsData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import com.increff.pos.pojo.OrderStatus;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
@Service
public class OrderDto {

    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductService productService;

    @Transactional(rollbackOn = ApiException.class)
    public String createOrder(OrderForm orderForm) throws ApiException {
        HelperDto.normalise(orderForm);
        OrderPojo orderPojo = HelperDto.convert(orderForm);
        orderService.createOrder(orderPojo);
        return orderPojo.getOrderCode();
    }

    @Transactional
    public List<OrderData> getAllOrders() {
        List<OrderPojo> orderPojoList = orderService.getAllOrders();
        List<OrderData> orderDataList = orderPojoList.stream()
                .map(HelperDto::convert)
                .collect(Collectors.toList());
        return orderDataList;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void deleteOrder(String orderCode) throws ApiException {
        Integer orderId = orderService.deleteOrder(orderCode);
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAllOrderItems(orderId);
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            InventoryPojo inventoryPojo = inventoryService.getProductInventoryByProductId(orderItemPojo.getProductId());
            int updatedQuantity = inventoryPojo.getQuantity() + orderItemPojo.getQuantity();
            inventoryService.updateProductInInventory(inventoryPojo, updatedQuantity);
        }
        orderItemService.deleteOrderItemsByOrder(orderId);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void changeOrderStatus(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderService.getOrderByOrderCode(orderCode);
        if (orderPojo.getStatus().equals(OrderStatus.CREATED)) {
            orderService.changeStatus(orderCode);
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderDetailsData getAllOrderDetails(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderService.getOrderByOrderCode(orderCode);
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAllOrderItems(orderPojo.getId());
        List<OrderItemData> orderItemDataList = new ArrayList<OrderItemData>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            ProductPojo productPojo = productService.getProductById(orderItemPojo.getProductId());
            orderItemDataList.add(HelperDto.convert(orderItemPojo, productPojo.getBarcode(), productPojo.getName()));
        }
        return HelperDto.convert(orderPojo, orderItemDataList);
    }


}
