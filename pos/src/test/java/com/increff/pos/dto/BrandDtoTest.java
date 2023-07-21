package com.increff.pos.dto;

import com.increff.pos.Helper;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.spring.AbstractUnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class BrandDtoTest extends AbstractUnitTest {

    @Autowired
    private BrandDto brandDto;
    @Autowired
    private BrandService brandService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testInsert() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1", "category 1");
        brandDto.insert(brandForm);
        BrandPojo brandPojo = brandService.getByBrandCategory("brand 1", "category 1");
        assertNotNull(brandPojo);
        assertEquals(brandPojo.getBrand(), "brand 1");
        assertEquals(brandPojo.getCategory(), "category 1");
    }

    @Test
    public void testDuplicateInsert() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1", "category 1");
        brandDto.insert(brandForm);
        BrandForm duplicateBrandForm = Helper.createBrandForm("brand 1", "category 1");
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("The brand-category pair brand 1-category 1 already exists");
        brandDto.insert(duplicateBrandForm);
    }

    @Test
    public void testUpdate() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1", "category 1");
        int id = brandDto.insert(brandForm);
        BrandForm updatedBrandForm = Helper.createBrandForm("brand 2", "category 2");
        brandDto.update(id, updatedBrandForm);
        BrandPojo brandPojo = brandService.getById(id);
        assertEquals(brandPojo.getBrand(), "brand 2");
        assertEquals(brandPojo.getCategory(), "category 2");
    }

    @Test
    public void testUpdateSameData() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1", "category 1");
        int id = brandDto.insert(brandForm);
        BrandForm updatedBrandForm = Helper.createBrandForm("brand 1", "category 1");
        brandDto.update(id, updatedBrandForm);
        BrandPojo brandPojo = brandService.getById(id);
        assertEquals(brandPojo.getBrand(), "brand 1");
        assertEquals(brandPojo.getCategory(), "category 1");
    }

    @Test
    public void testUpdateItemNotExists() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1", "category 1");
        int id = brandDto.insert(brandForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand item doesn't exist");
        brandDto.update(id + 1, brandForm);
    }

    @Test
    public void testUpdateDuplicateBrandCategory() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1", "category 1");
        brandDto.insert(brandForm);
        BrandForm brandForm1 = Helper.createBrandForm("brand 2", "category 2");
        int id = brandDto.insert(brandForm1);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("The brand-category pair brand 1-category 1 already exists");
        brandDto.update(id, brandForm);
    }

    @Test
    public void testSelectBrand() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1", "category 1");
        int id = brandDto.insert(brandForm);
        BrandData brandData = brandDto.getById(id);
        assertEquals(brandData.getId().intValue(), id);
        assertEquals(brandData.getBrand(), "brand 1");
        assertEquals(brandData.getCategory(), "category 1");
    }

    @Test
    public void testSelectBrandNotExists() throws ApiException {
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand item doesn't exist");
        brandDto.getById(1);
    }

    @Test
    public void testSelectAll() throws ApiException {
        List<BrandData> actualBrandDataList = new ArrayList<>();
        BrandForm brandForm = Helper.createBrandForm("brand 1", "category 1");
        int id = brandDto.insert(brandForm);
        actualBrandDataList.add(Helper.createBrandData(id, "brand 1", "category 1"));
        BrandForm brandForm1 = Helper.createBrandForm("brand 2", "category 2");
        int id1 = brandDto.insert(brandForm1);
        actualBrandDataList.add(Helper.createBrandData(id1, "brand 2", "category 2"));
        PaginatedData actualPaginatedData = new PaginatedData(actualBrandDataList, brandService.getCount());
        PaginatedData expectedPaginatedData = brandDto.getAll(1, 10);
        assertEquals(expectedPaginatedData.getDataList().size(), actualPaginatedData.getDataList().size());
        assertEquals(((BrandData) expectedPaginatedData.getDataList().get(0)).getBrand(), "brand 1");
        assertEquals(((BrandData) expectedPaginatedData.getDataList().get(0)).getCategory(), "category 1");
        assertEquals(((BrandData) expectedPaginatedData.getDataList().get(1)).getBrand(), "brand 2");
        assertEquals(((BrandData) expectedPaginatedData.getDataList().get(1)).getCategory(), "category 2");
    }

    @Test
    public void testBulkInsert() throws ApiException {
        List<BrandForm> brandFormList = new ArrayList<>();
        brandFormList.add(Helper.createBrandForm("brand 1", "category 1"));
        brandFormList.add(Helper.createBrandForm("brand 2", "category 2"));
        brandFormList.add(Helper.createBrandForm("brand 3", "category 3"));
        brandDto.insertList(brandFormList);
        List<BrandPojo> expectedBrandPojoList = brandService.getAll();
        assertEquals(expectedBrandPojoList.size(), 3);
        assertEquals(expectedBrandPojoList.get(0).getBrand(), "brand 1");
        assertEquals(expectedBrandPojoList.get(0).getCategory(), "category 1");
        assertEquals(expectedBrandPojoList.get(1).getBrand(), "brand 2");
        assertEquals(expectedBrandPojoList.get(1).getCategory(), "category 2");
        assertEquals(expectedBrandPojoList.get(2).getBrand(), "brand 3");
        assertEquals(expectedBrandPojoList.get(2).getCategory(), "category 3");

    }

    @Test
    public void testBulkInsertError(){
        List<BrandForm> brandFormList = new ArrayList<>();
        brandFormList.add(Helper.createBrandForm("brand 1", "category 1"));
        brandFormList.add(Helper.createBrandForm("brand 1", "category 1"));
        brandFormList.add(Helper.createBrandForm("brand 1", "category 1"));
        try {
            brandDto.insertList(brandFormList);
        } catch (ApiException e) {
            List<ErrorData> actualErrorDataList = new ArrayList<>();
            actualErrorDataList.add(Helper.createErrorData(2, "The brand-category pair brand 1-category 1 already exists"));
            actualErrorDataList.add(Helper.createErrorData(3, "The brand-category pair brand 1-category 1 already exists"));
            assertEquals(actualErrorDataList.size(), e.getErrorDataList().size());
            assertEquals(e.getErrorDataList().get(0).getErrorMessage(), "The brand-category pair brand 1-category 1 already exists");
            assertEquals(Optional.ofNullable(e.getErrorDataList().get(0).getRow()), Optional.ofNullable(2));
            assertEquals(e.getErrorDataList().get(1).getErrorMessage(), "The brand-category pair brand 1-category 1 already exists");
            assertEquals(Optional.ofNullable(e.getErrorDataList().get(1).getRow()), Optional.ofNullable(3));
        }
    }

}
