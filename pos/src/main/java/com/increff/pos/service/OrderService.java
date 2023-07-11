package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderStatus;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Service
@Log4j
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Transactional
    public void createOrder(OrderPojo orderPojo) {
        orderDao.createOrder(orderPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo getOrderByOrderCode(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderDao.getOrderByOrderCode(orderCode);
        checkOrder(orderPojo);
        return orderPojo;
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo getOrderByOrderId(int orderId) throws ApiException {
        OrderPojo orderPojo = orderDao.getOrderByOrderId(orderId);
        checkOrder(orderPojo);
        return orderPojo;
    }

    @Transactional
    public List<OrderPojo> getAllOrders() {
        return orderDao.getAllOrders();
    }

    @Transactional
    public List<OrderPojo> getOrderByDate(Date startTime, Date endTime) {
        return orderDao.getOrderByDate(startTime, endTime);
    }

    @Transactional(rollbackOn = ApiException.class)
    public Integer deleteOrder(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderDao.getOrderByOrderCode(orderCode);
        if (Objects.nonNull(orderPojo)) {
            if (orderPojo.getStatus().equals(OrderStatus.INVOICED)) {
                throw new ApiException("Cannot delete order!!");
            }
            orderDao.deleteOrder(orderCode);
            return orderPojo.getId();
        }
        return null;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void changeStatus(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderDao.getOrderByOrderCode(orderCode);
        checkOrder(orderPojo);
        orderPojo.setStatus(OrderStatus.INVOICED);
    }

    private void checkOrder(OrderPojo orderPojo) throws ApiException {
        if (Objects.isNull(orderPojo)) {
            throw new ApiException("Order doesn't exist!!");
        }
    }
}
