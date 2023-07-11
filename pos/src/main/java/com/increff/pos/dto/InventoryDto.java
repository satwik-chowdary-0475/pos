package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class InventoryDto {
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private BrandService brandService;

    @Transactional(rollbackOn = ApiException.class)
    public int insertProductInInventory(InventoryForm inventoryForm) throws ApiException {
        ProductPojo productPojo = productService.getProductByBarcode(inventoryForm.getBarcode());
        HelperDto.validate(inventoryForm);
        InventoryPojo inventoryPojo = HelperDto.convert(inventoryForm, productPojo.getId());
        return inventoryService.insertProductInInventory(inventoryPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateProductInInventory(int id, InventoryForm inventoryForm) throws ApiException {
        ProductPojo productPojo = productService.getProductById(id);
        HelperDto.validate(inventoryForm);
        validateProduct(id, inventoryForm.getBarcode());
        InventoryPojo inventoryPojo = HelperDto.convert(inventoryForm, productPojo.getId());
        inventoryService.updateProductInInventory(inventoryPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryData getProduct(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.getProductInventoryByProductId(id);
        ProductPojo productPojo = productService.getProductById(inventoryPojo.getProductId());
        return HelperDto.convert(inventoryPojo, productPojo.getBarcode(), productPojo.getName());
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<InventoryData> getAllProducts() throws ApiException {
        List<InventoryPojo> inventoryPojoList = inventoryService.getAllProductsInInventory();
        List<InventoryData> inventoryDataList = new ArrayList<InventoryData>();
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            ProductPojo productPojo = productService.getProductById(inventoryPojo.getProductId());
            inventoryDataList.add(HelperDto.convert(inventoryPojo, productPojo.getBarcode(), productPojo.getName()));
        }
        return inventoryDataList;
    }

    private void validateProduct(int id, String barcode) throws ApiException {
        ProductPojo productPojo = productService.getProductById(id);
        if (!productPojo.getBarcode().equals(barcode)) {
            throw new ApiException("Product id and barcode doesn't belong to same product!!");
        }
    }

    //TODO: for loops or streams??
    @Transactional(rollbackOn = ApiException.class)
    public void insertInventoryList(List<InventoryForm> inventoryFormList) throws ApiException {
        List<ErrorData> errorDataList = IntStream.range(0, inventoryFormList.size())
                .mapToObj(row -> {
                    InventoryForm inventoryForm = inventoryFormList.get(row);
                    try {
                        insertProductInInventory(inventoryForm);
                        return null;
                    } catch (ApiException e) {
                        return HelperDto.convert(row + 1, e.getMessage());
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!errorDataList.isEmpty()) {
            throw new ApiException(errorDataList);
        }
    }
}
