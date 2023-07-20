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
    public Integer insert(InventoryPojo inventoryPojo) throws ApiException {
        int productId = inventoryPojo.getProductId();
        int quantity = inventoryPojo.getQuantity();
        InventoryPojo existingInventoryPojo = inventoryDao.getById(productId);

        if (Objects.isNull(existingInventoryPojo)) {
            return insert(productId, quantity);
        }
        update(existingInventoryPojo, (quantity + existingInventoryPojo.getQuantity()));
        return existingInventoryPojo.getProductId();

    }

    @Transactional
    public Integer insert(Integer productId, Integer quantity) {
        InventoryPojo newInventoryPojo = new InventoryPojo();
        newInventoryPojo.setProductId(productId);
        newInventoryPojo.setQuantity(quantity);
        inventoryDao.insert(newInventoryPojo);
        return productId;
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo getById(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryDao.getById(id);
        if (Objects.isNull(inventoryPojo)) {
            throw new ApiException("Product not present in inventory");
        }
        return inventoryPojo;
    }

    @Transactional
    public List<InventoryPojo> getAll() {
        return inventoryDao.getAll();
    }

    @Transactional
    public List<InventoryPojo> getAll(int page, int rowsPerPage) {
        return inventoryDao.getAll(page, rowsPerPage);
    }

    @Transactional
    public Integer getCount() {
        return inventoryDao.getCount();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(InventoryPojo updatedInventoryPojo, String barcode) throws ApiException {
        InventoryPojo existingInventoryPojo = inventoryDao.getById(updatedInventoryPojo.getProductId());
        if (Objects.isNull(existingInventoryPojo)) {
            throw new ApiException("Product with barcode " + barcode + " not present in inventory");
        }

        existingInventoryPojo.setQuantity(updatedInventoryPojo.getQuantity());
    }

    @Transactional
    public void update(InventoryPojo inventoryPojo, int quantity) {
        inventoryPojo.setQuantity(quantity);
    }

}
