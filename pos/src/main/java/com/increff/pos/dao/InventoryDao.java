package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;


@Repository
public class InventoryDao extends AbstractDao {
    private static String SELECT_BY_ID = "select p from InventoryPojo p where id=:id";
    private static String SELECT_ALL = "select p from InventoryPojo p";
    private static String SELECT_ALL_COUNT = "select COUNT(p) from InventoryPojo p";

    @Transactional
    public void insert(InventoryPojo inventoryPojo) {
        em().persist(inventoryPojo);
    }

    @Transactional
    public InventoryPojo getById(int id) {
        TypedQuery<InventoryPojo> query = getQuery(SELECT_BY_ID, InventoryPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    @Transactional
    public List<InventoryPojo> getAll() {
        TypedQuery<InventoryPojo> query = getQuery(SELECT_ALL, InventoryPojo.class);
        return query.getResultList();
    }

    @Transactional
    public List<InventoryPojo> getAll(int page, int rowsPerPage) {
        TypedQuery<InventoryPojo> query = getQuery(SELECT_ALL, InventoryPojo.class);
        query.setFirstResult((page-1)*rowsPerPage);
        query.setMaxResults(rowsPerPage);
        return query.getResultList();
    }

    @Transactional
    public Integer getCount(){
        TypedQuery<Number> query = getQuery(SELECT_ALL_COUNT, Number.class);
        return query.getSingleResult().intValue();
    }

}
