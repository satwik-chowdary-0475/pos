package com.increff.pos.dto;

import com.increff.pos.Helper;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.spring.AbstractUnitTest;
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
        productDto.insertProduct(productForm);
        ProductForm productForm1 = Helper.createProductForm("barcode 3","brand 1","category 1","product 3",120.12);
        productDto.insertProduct(productForm1);

    }

    @Test
    public void TestInsert() throws ApiException {
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insertProductInInventory(inventoryForm);
        InventoryPojo inventoryPojo = inventoryService.getProductInventoryByProductId(id);
        assertEquals(inventoryPojo.getQuantity().intValue(),100);
        assertEquals(inventoryPojo.getProductId().intValue(),id);
    }

    @Test
    public void TestInsertNotExist() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 2",100);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with barcode barcode 2 doesn't exist!!");
        inventoryDto.insertProductInInventory(inventoryForm);
    }

    @Test
    public void TestInsertExistingItem() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        inventoryDto.insertProductInInventory(inventoryForm);
        InventoryForm newInventoryForm = Helper.createInventoryForm("barcode 1",200);
        int id = inventoryDto.insertProductInInventory(newInventoryForm);
        assertEquals(Optional.ofNullable(inventoryService.getProductInventoryByProductId(id).getQuantity()),Optional.ofNullable(300));
    }

    @Test
    public void TestUpdate() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insertProductInInventory(inventoryForm);
        InventoryForm updatedInventoryForm = Helper.createInventoryForm("barcode 1",200);
        inventoryDto.updateProductInInventory(id,updatedInventoryForm);
        InventoryPojo inventoryPojo = inventoryService.getProductInventoryByProductId(id);
        assertEquals(inventoryPojo.getQuantity().intValue(),200);
    }

    @Test
    public void TestUpdateNotExistsBarcode() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insertProductInInventory(inventoryForm);
        InventoryForm updatedInventoryForm = Helper.createInventoryForm("barcode 2",200);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product id and barcode doesn't belong to same product!!");
        inventoryDto.updateProductInInventory(id,updatedInventoryForm);
    }

    @Test
    public void TestUpdateNotExistsProduct() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insertProductInInventory(inventoryForm);
        InventoryForm updatedInventoryForm = Helper.createInventoryForm("barcode 3",200);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product not present in inventory!!");
        inventoryDto.updateProductInInventory(id+1,updatedInventoryForm);
    }

    @Test
    public void TestGetProduct() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insertProductInInventory(inventoryForm);
        InventoryData actualInventoryData = Helper.createInventoryData(id,"product 1","barcode 1",100);
        InventoryData expectedInventoryData = inventoryDto.getProduct(id);
        assertEquals(actualInventoryData.getProductName(),expectedInventoryData.getProductName());
        assertEquals(actualInventoryData.getBarcode(),expectedInventoryData.getBarcode());
        assertEquals(actualInventoryData.getQuantity(),expectedInventoryData.getQuantity());
        assertEquals(actualInventoryData.getProductId(),expectedInventoryData.getProductId());
    }

    @Test
    public void TestGetProductNotExistsInventory() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insertProductInInventory(inventoryForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product not present in inventory!!");
        InventoryData expectedInventoryData = inventoryDto.getProduct(id+1);
    }

    @Test
    public void TestGetAllProducts() throws ApiException{
        List<InventoryData> actualInventoryData = new ArrayList<InventoryData>();
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insertProductInInventory(inventoryForm);
        actualInventoryData.add(Helper.createInventoryData(id,"product 1","barcode 1",100));
        InventoryForm inventoryForm1 = Helper.createInventoryForm("barcode 3",100);
        int id1 = inventoryDto.insertProductInInventory(inventoryForm1);
        actualInventoryData.add(Helper.createInventoryData(id1,"product 3","barcode 3",100));
        List<InventoryData>expectedInventoryData = inventoryDto.getAllProducts();
        assertEquals(actualInventoryData.size(),expectedInventoryData.size());
    }

    @Test
    public void TestBulkInsert() throws ApiException{
        List<InventoryForm>inventoryFormList = new ArrayList<>();
        inventoryFormList.add(Helper.createInventoryForm("barcode 1",20));
        inventoryFormList.add(Helper.createInventoryForm("barcode 3",20));
        inventoryFormList.add(Helper.createInventoryForm("barcode 3",20));
        inventoryDto.insertInventoryList(inventoryFormList);
        List<InventoryPojo> inventoryPojoList = inventoryService.getAllProductsInInventory();
        assertEquals(inventoryPojoList.size(),2);
    }

    @Test
    public void TestBulkInsertError() throws ApiException{
        List<InventoryForm>inventoryFormList = new ArrayList<>();
        inventoryFormList.add(Helper.createInventoryForm("barcode 5",20));
        inventoryFormList.add(Helper.createInventoryForm("barcode 5",20));
        inventoryFormList.add(Helper.createInventoryForm("barcode 3",20));
        try {
            inventoryDto.insertInventoryList(inventoryFormList);
        } catch (ApiException e) {
            List<ErrorData>actualErrorDataList = new ArrayList<>();
            actualErrorDataList.add(Helper.createErrorData(1,"Product with barcode barcode 5 doesn't exist!!"));
            actualErrorDataList.add(Helper.createErrorData(2,"Product with barcode barcode 5 doesn't exist!!"));
            assertEquals(actualErrorDataList.size(),e.getErrorDataList().size());
        }

    }



}
