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
    private static String SELECT_ALL_COUNT = "select COUNT(p) from OrderPojo p";

    @Transactional
    public void insert(OrderPojo orderPojo) {
        em().persist(orderPojo);
    }

    @Transactional
    public List<OrderPojo> getByDate(Date startTime, Date endTime, OrderStatus status) {
        Query query = em().createQuery(SELECT_BY_DATE);
        query.setParameter("startTime", startTime, TemporalType.TIMESTAMP);
        query.setParameter("endTime", endTime, TemporalType.TIMESTAMP);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Transactional
    public OrderPojo getByOrderCode(String orderCode) {
        TypedQuery<OrderPojo> query = getQuery(SELECT_BY_ORDER_CODE, OrderPojo.class);
        query.setParameter("orderCode", orderCode);
        return getSingle(query);
    }

    @Transactional
    public OrderPojo getByOrderId(int id) {
        TypedQuery<OrderPojo> query = getQuery(SELECT_BY_ID, OrderPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    @Transactional
    public List<OrderPojo> getAll() {
        TypedQuery<OrderPojo> query = getQuery(SELECT_ALL, OrderPojo.class);
        return query.getResultList();
    }

    @Transactional
    public List<OrderPojo> getAll(int page,int rowsPerPage) {
        TypedQuery<OrderPojo> query = getQuery(SELECT_ALL, OrderPojo.class);
        query.setFirstResult((page-1)*rowsPerPage);
        query.setMaxResults(rowsPerPage);
        return query.getResultList();
    }

    @Transactional
    public int delete(String orderCode) {
        Query query = em().createQuery(DELETE_BY_ORDER_CODE);
        query.setParameter("orderCode", orderCode);
        query.setParameter("status", OrderStatus.CREATED);
        return query.executeUpdate();
    }

    @Transactional
    public Integer getCount() {
        TypedQuery<Number> query = em().createQuery(SELECT_ALL_COUNT,Number.class);
        return query.getSingleResult().intValue();
    }
}
