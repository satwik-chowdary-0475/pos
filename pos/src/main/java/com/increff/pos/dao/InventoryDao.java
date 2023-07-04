package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.BrandService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;


@Repository
public class InventoryDao extends AbstractDao{
    private static String SELECT_BY_ID = "select p from InventoryPojo p where id=:id";
    private static String SELECT_ALL = "select p from InventoryPojo p";

    @Transactional
    public void insert(InventoryPojo inventoryPojo){
        em().persist(inventoryPojo);
    }
    @Transactional
    public InventoryPojo select(int id){
        TypedQuery<InventoryPojo> query = getQuery(SELECT_BY_ID, InventoryPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }
    @Transactional
    public List<InventoryPojo> selectAll(){
        TypedQuery<InventoryPojo> query = getQuery(SELECT_ALL, InventoryPojo.class);
        return query.getResultList();
    }
}
