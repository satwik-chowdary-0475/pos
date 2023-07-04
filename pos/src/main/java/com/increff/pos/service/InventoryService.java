package com.increff.pos.service;


import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class InventoryService {
    @Autowired
    private InventoryDao inventoryDao;

    @Transactional(rollbackOn = ApiException.class)
    public Integer insert(InventoryPojo inventoryPojo) throws ApiException {
        int id = inventoryPojo.getId();
        int quantity = inventoryPojo.getQuantity();
        InventoryPojo existingInventoryPojo = inventoryDao.select(id);
        if(existingInventoryPojo == null){
            InventoryPojo newInventoryPojo = new InventoryPojo();
            newInventoryPojo.setId(id);
            newInventoryPojo.setQuantity(quantity);
            inventoryDao.insert(newInventoryPojo);
            return id;
        }
        else{
            int newQuantity = quantity + existingInventoryPojo.getQuantity();
            existingInventoryPojo.setQuantity(newQuantity);
            return existingInventoryPojo.getId();
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo select(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryDao.select(id);
        if(inventoryPojo == null){
            throw new ApiException("Product with id not present in inventory!!");
        }
        return inventoryPojo;
    }

    @Transactional
    public List<InventoryPojo> selectAll(){
        return inventoryDao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(InventoryPojo updatedInventoryPojo) throws ApiException {
        InventoryPojo existingInventoryPojo = inventoryDao.select(updatedInventoryPojo.getId());
        if(existingInventoryPojo == null){
            throw new ApiException("Product with id not present in inventory!!");
        }
        existingInventoryPojo.setQuantity(updatedInventoryPojo.getQuantity());
    }

    @Transactional
    public void update(InventoryPojo inventoryPojo,int quantity){
        inventoryPojo.setQuantity(quantity);
    }

}
