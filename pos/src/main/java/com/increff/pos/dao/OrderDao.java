package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderStatus;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

@Repository
@Log4j
public class OrderDao extends AbstractDao {

    private static String SELECT_BY_ID = "select p from OrderPojo p where id=:id";
    private static String DELETE_BY_ORDER_CODE = "delete from OrderPojo p where orderCode=:orderCode and status=:status";
    private static String SELECT_BY_ORDER_CODE = "select p from OrderPojo p where orderCode=:orderCode";
    private static String SELECT_ALL = "select p from OrderPojo p ORDER BY p.id DESC";
    private static String SELECT_BY_DATE = "select p from OrderPojo p where p.status =:status and DAY(p.updatedAt) >= DAY(:startTime) and DAY(p.updatedAt) <= DAY(:endTime)";


    @Transactional
    public void createOrder(OrderPojo orderPojo) {
        em().persist(orderPojo);
    }

    @Transactional
    public List<OrderPojo> getOrderByDate(Date startTime, Date endTime,OrderStatus status) {
        Query query = em().createQuery(SELECT_BY_DATE);
        query.setParameter("startTime", startTime, TemporalType.TIMESTAMP);
        query.setParameter("endTime", endTime, TemporalType.TIMESTAMP);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Transactional
    public OrderPojo getOrderByOrderCode(String orderCode) {
        TypedQuery<OrderPojo> query = getQuery(SELECT_BY_ORDER_CODE, OrderPojo.class);
        query.setParameter("orderCode", orderCode);
        return getSingle(query);
    }

    @Transactional
    public OrderPojo getOrderByOrderId(int id) {
        TypedQuery<OrderPojo> query = getQuery(SELECT_BY_ID, OrderPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    @Transactional
    public List<OrderPojo> getAllOrders() {
        TypedQuery<OrderPojo> query = getQuery(SELECT_ALL, OrderPojo.class);
        return query.getResultList();
    }

    @Transactional
    public int deleteOrder(String orderCode) {
        Query query = em().createQuery(DELETE_BY_ORDER_CODE);
        query.setParameter("orderCode", orderCode);
        query.setParameter("status", OrderStatus.CREATED);
        return query.executeUpdate();
    }

}
