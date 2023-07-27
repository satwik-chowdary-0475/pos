package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;


@Repository
@Transactional
public class InventoryDao extends AbstractDao {
    private static final String SELECT_BY_PRODUCT_ID = "select p from InventoryPojo p where productId=:productId";
    private static final String SELECT_ALL = "select p from InventoryPojo p";
    private static final String SELECT_ALL_COUNT = "select COUNT(p) from InventoryPojo p";

    public void insert(InventoryPojo inventoryPojo) {
        em().persist(inventoryPojo);
    }

    public InventoryPojo getByProductId(int productId) {
        TypedQuery<InventoryPojo> query = getQuery(SELECT_BY_PRODUCT_ID, InventoryPojo.class);
        query.setParameter("productId", productId);
        return getSingle(query);
    }

    public List<InventoryPojo> getAll() {
        TypedQuery<InventoryPojo> query = getQuery(SELECT_ALL, InventoryPojo.class);
        return query.getResultList();
    }

    public List<InventoryPojo> getAll(int offset, int rowsPerPage) {
        TypedQuery<InventoryPojo> query = getQuery(SELECT_ALL, InventoryPojo.class);
        query.setFirstResult(offset);
        query.setMaxResults(rowsPerPage);
        return query.getResultList();
    }

    public Integer getCount() {
        TypedQuery<Number> query = getQuery(SELECT_ALL_COUNT, Number.class);
        return query.getSingleResult().intValue();
    }

}
