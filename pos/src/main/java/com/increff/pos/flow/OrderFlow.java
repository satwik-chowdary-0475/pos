package com.increff.pos.flow;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderFlow {

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;

    public void delete(Integer orderId) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAllByOrderId(orderId);
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            updateInventory(orderItemPojo);
        }

        orderItemService.deleteByOrderId(orderId);
    }

    private void updateInventory(OrderItemPojo orderItemPojo) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.getByProductId(orderItemPojo.getProductId());
        int updatedQuantity = inventoryPojo.getQuantity() + orderItemPojo.getQuantity();

        inventoryService.update(inventoryPojo, updatedQuantity);
    }

}
