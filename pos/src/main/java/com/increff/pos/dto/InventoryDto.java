package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.InventoryUpdateForm;
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

    public InventoryDto() {
    }

    @Transactional(rollbackOn = ApiException.class)
    public int insert(InventoryForm inventoryForm) throws ApiException {
        HelperDto.validate(inventoryForm);
        ProductPojo productPojo = productService.getProductByBarcode(inventoryForm.getBarcode());

        InventoryPojo inventoryPojo = HelperDto.convert(inventoryForm, productPojo.getId());
        return inventoryService.insert(inventoryPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(int id, InventoryUpdateForm inventoryUpdateForm) throws ApiException {
        HelperDto.validate(inventoryUpdateForm);
        ProductPojo productPojo = productService.getProductById(id);

        InventoryPojo inventoryPojo = HelperDto.convert(inventoryUpdateForm, productPojo.getId());
        inventoryService.update(inventoryPojo, productPojo.getBarcode());
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryData getById(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.getById(id);
        ProductPojo productPojo = productService.getProductById(inventoryPojo.getProductId());

        return HelperDto.convert(inventoryPojo, productPojo.getBarcode(), productPojo.getName());
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<InventoryData> getAll() throws ApiException {
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();

        List<InventoryData> inventoryDataList = new ArrayList<InventoryData>();
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            ProductPojo productPojo = productService.getProductById(inventoryPojo.getProductId());
            inventoryDataList.add(HelperDto.convert(inventoryPojo, productPojo.getBarcode(), productPojo.getName()));
        }

        return inventoryDataList;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void insertList(List<InventoryForm> inventoryFormList) throws ApiException {
        List<ErrorData> errorDataList = IntStream.range(0, inventoryFormList.size())
                .mapToObj(row -> {
                    InventoryForm inventoryForm = inventoryFormList.get(row);
                    try {
                        insert(inventoryForm);
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
