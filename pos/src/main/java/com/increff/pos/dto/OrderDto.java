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
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.transaction.Transactional;
import java.util.*;

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
    public String insert(OrderForm orderForm) throws ApiException {
        HelperDto.normalise(orderForm);
        OrderPojo orderPojo = HelperDto.convert(orderForm);
        orderService.insert(orderPojo);
        return orderPojo.getOrderCode();
    }

    @Transactional
    public List<OrderData>getAllOrders(){
        List<OrderData>orderDataList = new ArrayList<OrderData>();
        List<OrderPojo>orderPojoList = orderService.selectAll();
        for(OrderPojo orderPojo:orderPojoList){
            orderDataList.add(HelperDto.convert(orderPojo));
        }
        return orderDataList;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(String orderCode) throws ApiException{
        Integer orderId = orderService.delete(orderCode);
        List<OrderItemPojo>orderItemPojoList = orderItemService.selectAll(orderId);
        for(OrderItemPojo orderItemPojo : orderItemPojoList){
            InventoryPojo inventoryPojo = inventoryService.select(orderItemPojo.getProductId());
            int updatedQuantity = inventoryPojo.getQuantity() + orderItemPojo.getQuantity();
            inventoryService.update(inventoryPojo,updatedQuantity);
        }
        orderItemService.deleteByOrder(orderId);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void changeStatus(String orderCode) throws ApiException{
        OrderPojo orderPojo = orderService.select(orderCode);
        if(orderPojo.getStatus().equals("ACTIVE")){
            orderService.changeStatus(orderCode);
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderDetailsData getAllDetails(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderService.select(orderCode);
        List<OrderItemPojo>orderItemPojoList = orderItemService.selectAll(orderPojo.getId());
        List<OrderItemData>orderItemDataList = new ArrayList<OrderItemData>();
        for(OrderItemPojo orderItemPojo: orderItemPojoList){
            ProductPojo productPojo = productService.select(orderItemPojo.getProductId());
            orderItemDataList.add(HelperDto.convert(orderItemPojo,productPojo.getBarcode(),productPojo.getName()));
        }
        return HelperDto.convert(orderPojo,orderItemDataList);
    }


}
