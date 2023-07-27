package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Transactional
    public void insert(OrderPojo orderPojo) {
        orderDao.insert(orderPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo getByOrderCode(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderDao.getByOrderCode(orderCode);
        checkOrder(orderPojo);
        return orderPojo;
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo getByOrderId(int orderId) throws ApiException {
        OrderPojo orderPojo = orderDao.getByOrderId(orderId);
        checkOrder(orderPojo);
        checkStatus(orderPojo);
        return orderPojo;
    }

    @Transactional
    public List<OrderPojo> getAll(int page, int rowsPerPage) {
        return orderDao.getAll((page - 1) * rowsPerPage, rowsPerPage);
    }

    @Transactional
    public List<OrderPojo> getByDate(Date startTime, Date endTime) {
        return orderDao.getByDate(startTime, endTime, OrderStatus.INVOICED);
    }

    @Transactional(rollbackOn = ApiException.class)
    public Integer delete(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderDao.getByOrderCode(orderCode);

        if (Objects.nonNull(orderPojo)) {
            int orderId = orderPojo.getId();
            checkStatus(orderPojo);
            orderDao.delete(orderCode);
            return orderId;
        }

        return null;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void changeStatus(OrderPojo orderPojo, OrderStatus status) throws ApiException {
        checkOrder(orderPojo);
        checkStatus(orderPojo);
        orderPojo.setStatus(status);
    }

    @Transactional
    public Integer getCount() {
        return orderDao.getCount();
    }

    private void checkOrder(OrderPojo orderPojo) throws ApiException {
        if (Objects.isNull(orderPojo)) {
            throw new ApiException("Order doesn't exist");
        }
    }

    private void checkStatus(OrderPojo orderPojo) throws ApiException {
        if (orderPojo.getStatus().equals(OrderStatus.INVOICED)) {
            throw new ApiException("Cannot modify order");
        }
    }

}
