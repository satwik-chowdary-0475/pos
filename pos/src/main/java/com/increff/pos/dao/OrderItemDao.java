package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class OrderItemDao extends AbstractDao {
    private static final String SELECT_BY_ID = "select p from OrderItemPojo p where id=:id";
    private static final String SELECT_BY_PRODUCT_ID = "select p from OrderItemPojo p where orderId=:orderId and productId=:productId";
    private static final String SELECT_ALL = "select p from OrderItemPojo p where orderId=:orderId";
    private static final String DELETE_BY_ID = "delete from OrderItemPojo p where id=:id";
    private static final String DELETE_BY_ORDER_ID = "delete from OrderItemPojo p where orderId=:orderId";
    private static final String SELECT_ALL_REPORT = "SELECT SUM(p.quantity), SUM(p.quantity * p.sellingPrice) FROM OrderItemPojo p WHERE p.orderId IN :orderIdsList";
    private static final String SELECT_BY_ORDER_LIST = "select p from OrderItemPojo p where p.orderId IN :orderIdsList";

    public void insert(OrderItemPojo orderItemPojo) {
        em().persist(orderItemPojo);
    }

    public OrderItemPojo getById(int id) {
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_ID, OrderItemPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public OrderItemPojo getByProductId(int orderId, int productId) {
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_PRODUCT_ID, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        query.setParameter("productId", productId);
        return getSingle(query);
    }

    public Object[] getOrderItemsReport(List<Integer> orderIdsList) {
        TypedQuery<Object[]> query = em().createQuery(SELECT_ALL_REPORT, Object[].class);
        query.setParameter("orderIdsList", orderIdsList);
        return query.getSingleResult();
    }

    public List<OrderItemPojo> getAllByOrderId(int orderId) {
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_ALL, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }

    public int delete(int id) {
        Query query = em().createQuery(DELETE_BY_ID);
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public int deleteByOrderId(int orderId) {
        Query query = em().createQuery(DELETE_BY_ORDER_ID);
        query.setParameter("orderId", orderId);
        return query.executeUpdate();
    }

    public List<OrderItemPojo> getAllByOrderList(List<Integer> orderIdsList) {
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_ORDER_LIST, OrderItemPojo.class);
        query.setParameter("orderIdsList", orderIdsList);
        return query.getResultList();
    }

}
