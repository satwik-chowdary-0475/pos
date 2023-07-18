package com.increff.pos.controller;


import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.InventoryUpdateForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryDto inventoryDto;

    @ApiOperation(value = "Inserts product to inventory")
    @PostMapping(path = "")
    public void insertProductInInventory(@RequestBody InventoryForm inventoryForm) throws ApiException {
        inventoryDto.insert(inventoryForm);
    }

    @ApiOperation(value = "Updates a product in inventory")
    @PutMapping(path = "/{id}")
    public void updateProductInInventory(@PathVariable int id, @RequestBody InventoryUpdateForm inventoryUpdateForm) throws ApiException {
        inventoryDto.update(id, inventoryUpdateForm);
    }

    @ApiOperation(value = "Get list of all products in inventory")
    @GetMapping(path = "")
    public List<InventoryData> getAllProducts() throws ApiException {
        return inventoryDto.getAll();
    }

    @ApiOperation(value = "Get a product from inventory")
    @GetMapping(path = "/{id}")
    public InventoryData getProduct(@PathVariable int id) throws ApiException {
        return inventoryDto.getById(id);
    }

    @ApiOperation(value = "Add list of inventory")
    @PostMapping(path = "/bulk")
    public void insertInventoryList(@RequestBody List<InventoryForm> inventoryFormList) throws ApiException {
        inventoryDto.insertList(inventoryFormList);
    }

}
