package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderDetailsData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import com.increff.pos.pojo.OrderStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrderDto {

    @Autowired
    private OrderFlow orderFlow;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductService productService;

    public String insert(OrderForm orderForm) throws ApiException {
        HelperDto.normalise(orderForm);
        OrderPojo orderPojo = HelperDto.convert(orderForm);

        orderService.insert(orderPojo);
        return orderPojo.getOrderCode();
    }

    public PaginatedData getAll(int page, int rowsPerPage) {
        List<OrderPojo> orderPojoList = orderService.getAll(page,rowsPerPage);
        Integer totalCount = orderService.getCount();

        List<OrderData> orderDataList = orderPojoList.stream()
                .map(HelperDto::convert)
                .collect(Collectors.toList());

        return new PaginatedData(orderDataList,totalCount);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(String orderCode) throws ApiException {
        Integer orderId = orderService.delete(orderCode);

        orderFlow.delete(orderId);
    }

    public void changeStatus(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderService.getByOrderCode(orderCode);

        orderService.changeStatus(orderPojo,OrderStatus.INVOICED);
    }

    public OrderDetailsData getAllOrderDetails(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderService.getByOrderCode(orderCode);
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAllByOrderId(orderPojo.getId());

        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            ProductPojo productPojo = productService.getById(orderItemPojo.getProductId());
            orderItemDataList.add(HelperDto.convert(orderItemPojo, productPojo.getBarcode(), productPojo.getName()));
        }

        return HelperDto.convert(orderPojo, orderItemDataList);
    }



}
