package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.pojo.OrderPojo;
import lombok.extern.log4j.Log4j;
import org.apache.xpath.operations.Or;
import org.hibernate.criterion.Order;
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
    public void insert(OrderPojo orderPojo){
        orderDao.insert(orderPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(String orderCode,OrderPojo newOrderPojo) throws ApiException {
        OrderPojo existingOrderPojo = orderDao.select(orderCode);
        if(existingOrderPojo == null){
            throw new ApiException("Cannot update order as order doesn't exist!!");
        }
        existingOrderPojo.setTime(newOrderPojo.getTime());
        existingOrderPojo.setStatus(newOrderPojo.getStatus());
        existingOrderPojo.setCustomerName(newOrderPojo.getCustomerName());
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo select(String orderCode) throws ApiException{
        OrderPojo orderPojo = orderDao.select(orderCode);
        if(orderPojo == null){
            throw new ApiException("Order doesn't exist!!");
        }
        return orderPojo;
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo select(int orderId) throws ApiException{
        OrderPojo orderPojo = orderDao.select(orderId);
        if(orderPojo == null){
            throw new ApiException("Order doesn't exist!!");
        }
        return orderPojo;
    }

    @Transactional
    public List<OrderPojo> selectAll(){
        return orderDao.selectAll();
    }

    @Transactional
    public List<OrderPojo> selectByDate(Date startTime,Date endTime){
        return orderDao.selectByDate(startTime,endTime);
    }

    @Transactional(rollbackOn = ApiException.class)
    public Integer delete(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderDao.select(orderCode);
        if(Objects.nonNull(orderPojo)){
            if(orderPojo.getStatus().equals("INVOICED")){
                throw new ApiException("Cannot delete order!!");
            }
            orderDao.delete(orderCode);
            return orderPojo.getId();
        }
        return null;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void changeStatus(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderDao.select(orderCode);
        if(orderPojo == null){
            throw new ApiException("Order doesn't exist!!");
        }
        orderPojo.setStatus("INVOICED");
    }
}
