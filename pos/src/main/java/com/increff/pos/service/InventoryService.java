package com.increff.pos.service;


import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class InventoryService {
    @Autowired
    private InventoryDao inventoryDao;

    @Transactional(rollbackOn = ApiException.class)
    public Integer insertProductInInventory(InventoryPojo inventoryPojo) throws ApiException {
        int id = inventoryPojo.getId();
        int quantity = inventoryPojo.getQuantity();
        InventoryPojo existingInventoryPojo = inventoryDao.getProductInventoryById(id);
        if (Objects.isNull(existingInventoryPojo)) {
            InventoryPojo newInventoryPojo = new InventoryPojo();
            newInventoryPojo.setId(id);
            newInventoryPojo.setQuantity(quantity);
            inventoryDao.insertProductInInventory(newInventoryPojo);
            return id;
        } else {
            int newQuantity = quantity + existingInventoryPojo.getQuantity();
            existingInventoryPojo.setQuantity(newQuantity);
            return existingInventoryPojo.getId();
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo getProductInventoryByProductId(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryDao.getProductInventoryById(id);
        if (Objects.isNull(inventoryPojo)) {
            throw new ApiException("Product with id not present in inventory!!");
        }
        return inventoryPojo;
    }

    @Transactional
    public List<InventoryPojo> getAllProductsInInventory() {
        return inventoryDao.getAllProductsInInventory();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateProductInInventory(InventoryPojo updatedInventoryPojo) throws ApiException {
        InventoryPojo existingInventoryPojo = inventoryDao.getProductInventoryById(updatedInventoryPojo.getId());
        if (Objects.isNull(existingInventoryPojo)) {
            throw new ApiException("Product with id not present in inventory!!");
        }
        existingInventoryPojo.setQuantity(updatedInventoryPojo.getQuantity());
    }

    @Transactional
    public void updateProductInInventory(InventoryPojo inventoryPojo, int quantity) {
        inventoryPojo.setQuantity(quantity);
    }

}
