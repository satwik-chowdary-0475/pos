package com.increff.pos.controller;


import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.constants.PaginationConstant;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedData;
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
    @PutMapping(path = "/{productId}")
    public void updateProductInInventory(@PathVariable int productId,
                                         @RequestBody InventoryUpdateForm inventoryUpdateForm
    ) throws ApiException {
        inventoryDto.update(productId, inventoryUpdateForm);
    }

    @ApiOperation(value = "Get list of all products in inventory")
    @GetMapping(path = "")
    public PaginatedData getAllProducts(
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_ROWS_PER_PAGE) int rowsPerPage
    ) throws ApiException {
        return inventoryDto.getAll(page, rowsPerPage);
    }

    @ApiOperation(value = "Get a product from inventory")
    @GetMapping(path = "/{productId}")
    public InventoryData getProduct(@PathVariable int productId) throws ApiException {
        return inventoryDto.getByProductId(productId);
    }

    @ApiOperation(value = "Add list of inventory")
    @PostMapping(path = "/bulk")
    public void insertInventoryList(@RequestBody List<InventoryForm> inventoryFormList) throws ApiException {
        inventoryDto.insertList(inventoryFormList);
    }

}
