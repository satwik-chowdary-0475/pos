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
        int productId = inventoryPojo.getProductId();
        int quantity = inventoryPojo.getQuantity();
        InventoryPojo existingInventoryPojo = inventoryDao.getProductInventoryById(productId);
        if (Objects.isNull(existingInventoryPojo)) {
            return insertNewProductInInventory(productId, quantity);
        } else {
            updateProductInInventory(existingInventoryPojo, (quantity + existingInventoryPojo.getQuantity()));
            return existingInventoryPojo.getProductId();
        }
    }

    @Transactional
    public Integer insertNewProductInInventory(Integer productId, Integer quantity) {
        InventoryPojo newInventoryPojo = new InventoryPojo();
        newInventoryPojo.setProductId(productId);
        newInventoryPojo.setQuantity(quantity);
        inventoryDao.insertProductInInventory(newInventoryPojo);
        return productId;
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
        InventoryPojo existingInventoryPojo = inventoryDao.getProductInventoryById(updatedInventoryPojo.getProductId());
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
