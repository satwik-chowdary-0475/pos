package com.increff.pos.dto;

import com.increff.pos.Helper;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.InventoryUpdateForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.spring.AbstractUnitTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;

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
        brandDto.insert(brandForm);
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        productDto.insert(productForm);
        ProductForm productForm1 = Helper.createProductForm("barcode 3","brand 1","category 1","product 3",120.12);
        productDto.insert(productForm1);

    }

    @Test
    public void TestInsert() throws ApiException {
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insert(inventoryForm);
        InventoryPojo inventoryPojo = inventoryService.getById(id);
        assertEquals(inventoryPojo.getQuantity().intValue(),100);
        assertEquals(inventoryPojo.getProductId().intValue(),id);
    }

    @Test
    public void TestInsertNotExist() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 2",100);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with barcode barcode 2 doesn't exist");
        inventoryDto.insert(inventoryForm);
    }

    @Test
    public void TestInsertExistingItem() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        inventoryDto.insert(inventoryForm);
        InventoryForm newInventoryForm = Helper.createInventoryForm("barcode 1",200);
        int id = inventoryDto.insert(newInventoryForm);
        assertEquals(Optional.ofNullable(inventoryService.getById(id).getQuantity()),Optional.ofNullable(300));
    }

    @Test
    public void TestUpdate() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insert(inventoryForm);
        InventoryUpdateForm updatedInventoryForm = Helper.createInventoryUpdateForm(200);
        inventoryDto.update(id,updatedInventoryForm);
        InventoryPojo inventoryPojo = inventoryService.getById(id);
        assertEquals(inventoryPojo.getQuantity().intValue(),200);
    }


    @Test
    public void TestUpdateNotExistsProduct() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insert(inventoryForm);
        InventoryUpdateForm updatedInventoryForm = Helper.createInventoryUpdateForm(200);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with barcode barcode 3 not present in inventory");
        inventoryDto.update(id+1,updatedInventoryForm);
    }

    @Test
    public void TestGetProduct() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insert(inventoryForm);
        InventoryData actualInventoryData = Helper.createInventoryData(id,"product 1","barcode 1",100);
        InventoryData expectedInventoryData = inventoryDto.getById(id);
        assertEquals(actualInventoryData.getProductName(),expectedInventoryData.getProductName());
        assertEquals(actualInventoryData.getBarcode(),expectedInventoryData.getBarcode());
        assertEquals(actualInventoryData.getQuantity(),expectedInventoryData.getQuantity());
        assertEquals(actualInventoryData.getProductId(),expectedInventoryData.getProductId());
    }

    @Test
    public void TestGetProductNotExistsInventory() throws ApiException{
        InventoryForm inventoryForm = Helper.createInventoryForm("barcode 1",100);
        int id = inventoryDto.insert(inventoryForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product not present in inventory");
        InventoryData expectedInventoryData = inventoryDto.getById(id+1);
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
        PaginatedData actualPaginatedData = new PaginatedData(actualInventoryData,inventoryService.getCount());
        PaginatedData expectedPaginatedData = inventoryDto.getAll(1,10);
        assertEquals(actualPaginatedData.getDataList().size(),expectedPaginatedData.getDataList().size());
    }

    @Test
    public void TestBulkInsert() throws ApiException{
        List<InventoryForm>inventoryFormList = new ArrayList<>();
        inventoryFormList.add(Helper.createInventoryForm("barcode 1",20));
        inventoryFormList.add(Helper.createInventoryForm("barcode 3",20));
        inventoryFormList.add(Helper.createInventoryForm("barcode 3",20));
        inventoryDto.insertList(inventoryFormList);
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        assertEquals(inventoryPojoList.size(),2);
    }

    @Test
    public void TestBulkInsertError() throws ApiException{
        List<InventoryForm>inventoryFormList = new ArrayList<>();
        inventoryFormList.add(Helper.createInventoryForm("barcode 5",20));
        inventoryFormList.add(Helper.createInventoryForm("barcode 5",20));
        inventoryFormList.add(Helper.createInventoryForm("barcode 3",20));
        try {
            inventoryDto.insertList(inventoryFormList);
        } catch (ApiException e) {
            List<ErrorData>actualErrorDataList = new ArrayList<>();
            actualErrorDataList.add(Helper.createErrorData(1,"Product with barcode barcode 5 doesn't exist"));
            actualErrorDataList.add(Helper.createErrorData(2,"Product with barcode barcode 5 doesn't exist"));
            assertEquals(actualErrorDataList.size(),e.getErrorDataList().size());
        }

    }



}
