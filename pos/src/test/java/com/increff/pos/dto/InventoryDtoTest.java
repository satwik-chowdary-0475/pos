package com.increff.pos.dto;

import com.google.protobuf.Api;
import com.increff.pos.Helper;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.spring.AbstractUnitTest;
import io.swagger.models.auth.In;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

public class InventoryDtoTest extends AbstractUnitTest {

    @Autowired
    private BrandDto brandDto;
    @Autowired
    private ProductDto productDto;
    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void init() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1","category 1");
        brandDto.insertBrand(brandForm);
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        productDto.insert(productForm);
        ProductForm productForm1 = Helper.createProductForm("barcode 3","brand 1","category 1","product 3",120.12);
        productDto.insert(productForm1);

    }

    // TODO : TO TEST HELPERS AND NORMALISE CASES
    @Test
    public void TestInsert() throws ApiException {
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insert(inventoryForm);
        InventoryPojo inventoryPojo = inventoryService.select(id);
        assertEquals(inventoryPojo.getQuantity().intValue(),100);
        assertEquals(inventoryPojo.getId().intValue(),id);
    }

    @Test
    public void TestInsertNotExist() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 2",100);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with given barcode doesn't exist!!");
        inventoryDto.insert(inventoryForm);
    }

    @Test
    public void TestInsertExistingItem() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        inventoryDto.insert(inventoryForm);
        InventoryForm newInventoryForm = Helper.createInventoryForm("barcode 1",200);
        int id = inventoryDto.insert(newInventoryForm);
        assertEquals(Optional.ofNullable(inventoryService.select(id).getQuantity()),Optional.ofNullable(300));
    }

    @Test
    public void TestUpdate() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insert(inventoryForm);
        InventoryForm updatedInventoryForm = Helper.createInventoryForm("barcode 1",200);
        inventoryDto.update(id,updatedInventoryForm);
        InventoryPojo inventoryPojo = inventoryService.select(id);
        assertEquals(inventoryPojo.getQuantity().intValue(),200);
    }

    @Test
    public void TestUpdateNotExistsBarcode() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insert(inventoryForm);
        InventoryForm updatedInventoryForm = Helper.createInventoryForm("barcode 2",200);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product id and barcode doesn't belong to same product!!");
        inventoryDto.update(id,updatedInventoryForm);
    }

    @Test
    public void TestUpdateNotExistsProduct() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insert(inventoryForm);
        InventoryForm updatedInventoryForm = Helper.createInventoryForm("barcode 3",200);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with id not present in inventory!!");
        inventoryDto.update(id+1,updatedInventoryForm);
    }

    @Test
    public void TestGetProduct() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insert(inventoryForm);
        InventoryData actualInventoryData = Helper.createInventoryData(id,"product 1","barcode 1",100);
        InventoryData expectedInventoryData = inventoryDto.getProduct(id);
        assertEquals(actualInventoryData.getProductName(),expectedInventoryData.getProductName());
        assertEquals(actualInventoryData.getBarcode(),expectedInventoryData.getBarcode());
        assertEquals(actualInventoryData.getQuantity(),expectedInventoryData.getQuantity());
        assertEquals(actualInventoryData.getId(),expectedInventoryData.getId());
    }

    @Test
    public void TestGetProductNotExistsInventory() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insert(inventoryForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with id not present in inventory!!");
        InventoryData expectedInventoryData = inventoryDto.getProduct(id+1);
    }

    @Test
    public void TestGetAllProducts() throws ApiException{
        List<InventoryData> actualInventoryData = new ArrayList<InventoryData>();
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insert(inventoryForm);
        actualInventoryData.add(Helper.createInventoryData(id,"product 1","barcode 1",100));
        InventoryForm inventoryForm1 = Helper.createInventoryForm("barcode 3",100);
        int id1 = inventoryDto.insert(inventoryForm1);
        actualInventoryData.add(Helper.createInventoryData(id1,"product 3","barcode 3",100));
        List<InventoryData>expectedInventoryData = inventoryDto.getAllProducts();
        assertEquals(actualInventoryData.size(),expectedInventoryData.size());
    }



}