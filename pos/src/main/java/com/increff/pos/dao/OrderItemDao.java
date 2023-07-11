package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Log4j
public class OrderItemDao extends AbstractDao {
    private static String SELECT_BY_ID = "select p from OrderItemPojo p where id=:id and orderId=:orderId";
    private static String SELECT_BY_PRODUCT_ID = "select p from OrderItemPojo p where orderId=:orderId and productId=:productId";
    private static String SELECT_ALL = "select p from OrderItemPojo p where orderId=:orderId";
    private static String DELETE_BY_ID = "delete from OrderItemPojo p where orderId=:orderId and id=:id";
    private static String DELETE_BY_ORDER_ID = "delete from OrderItemPojo p where orderId=:orderId";
    private static String SELECT_ALL_REPORT = "select SUM(p.quantity), SUM(p.quantity*p.sellingPrice) from OrderItemPojo p where p.orderId = :orderId";

    @Transactional
    public void insertOrderItem(OrderItemPojo orderItemPojo) {
        em().persist(orderItemPojo);
    }

    @Transactional
    public OrderItemPojo getOrderItemById(int orderId, int id) {
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_ID, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        query.setParameter("id", id);
        return getSingle(query);
    }

    @Transactional
    public OrderItemPojo getOrderItemByProductId(int orderId, int productId) {
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_PRODUCT_ID, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        query.setParameter("productId", productId);
        return getSingle(query);
    }

    @Transactional
    public Object[] getOrderItemsReport(int orderId) {
        TypedQuery<Object[]> query = em().createQuery(SELECT_ALL_REPORT, Object[].class);
        query.setParameter("orderId", orderId);
        Object[] result = query.getSingleResult();
        return result;
    }

    @Transactional
    public List<OrderItemPojo> getAllOrderItems(int orderId) {
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_ALL, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }


    @Transactional
    public int deleteOrderItem(int orderId, int id) {
        Query query = em().createQuery(DELETE_BY_ID);
        query.setParameter("id", id);
        query.setParameter("orderId", orderId);
        return query.executeUpdate();
    }

    @Transactional
    public int deleteAllOrderItems(int orderId) {
        Query query = em().createQuery(DELETE_BY_ORDER_ID);
        query.setParameter("orderId", orderId);
        return query.executeUpdate();
    }

}
