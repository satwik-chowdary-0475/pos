package com.increff.pos.service;


import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackOn = ApiException.class)
public class InventoryService {
    @Autowired
    private InventoryDao inventoryDao;

    public Integer insert(InventoryPojo inventoryPojo) throws ApiException {
        int productId = inventoryPojo.getProductId();
        int quantity = inventoryPojo.getQuantity();
        InventoryPojo existingInventoryPojo = inventoryDao.getByProductId(productId);

        if (Objects.isNull(existingInventoryPojo)) {
            inventoryDao.insert(inventoryPojo);
            return productId;
        }

        update(existingInventoryPojo, (quantity + existingInventoryPojo.getQuantity()));
        return existingInventoryPojo.getProductId();
    }

    public InventoryPojo getByProductId(int productId) throws ApiException {
        InventoryPojo inventoryPojo = inventoryDao.getByProductId(productId);
        if (Objects.isNull(inventoryPojo)) {
            throw new ApiException("Product not present in inventory");
        }

        return inventoryPojo;
    }

    public List<InventoryPojo> getAll() {
        return inventoryDao.getAll();
    }

    public List<InventoryPojo> getAll(int page, int rowsPerPage) {
        return inventoryDao.getAll((page - 1) * rowsPerPage, rowsPerPage);
    }

    public Integer getCount() {
        return inventoryDao.getCount();
    }

    public void update(InventoryPojo inventoryPojo, String barcode) throws ApiException {
        InventoryPojo existingInventoryPojo = inventoryDao.getByProductId(inventoryPojo.getProductId());
        if (Objects.isNull(existingInventoryPojo)) {
            throw new ApiException("Product with barcode " + barcode + " not present in inventory");
        }

        existingInventoryPojo.setQuantity(inventoryPojo.getQuantity());
    }

    public void update(InventoryPojo inventoryPojo, int quantity) {
        inventoryPojo.setQuantity(quantity);
    }

}
