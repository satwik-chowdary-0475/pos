package com.increff.pos.dto;

import com.increff.pos.Helper;
import com.increff.pos.model.data.BrandData;
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

import static org.junit.Assert.*;

public class BrandDtoTest extends AbstractUnitTest {

    @Autowired
    private BrandDto brandDto;
    @Autowired
    private BrandService brandService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    // TODO : TO TEST HELPERS AND NORMALISE CASES

    @Test
    public void testInsert() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1","category 1");
        int id = brandDto.insertBrand(brandForm);
        BrandPojo brandPojo = brandService.select("brand 1","category 1");
        assertNotNull(brandPojo);
        assertEquals(brandPojo.getBrand(),"brand 1");
        assertEquals(brandPojo.getCategory(),"category 1");
    }

    @Test
    public void testDuplicateInsert() throws ApiException{
        BrandForm brandForm = Helper.createBrandForm("brand 1","category 1");
        int id = brandDto.insertBrand(brandForm);
        BrandForm duplicateBrandForm = Helper.createBrandForm("brand 1","category 1");
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("brand-category pair already exist!!");
        brandDto.insertBrand(duplicateBrandForm);
    }

    @Test
    public void testUpdate() throws ApiException{
        BrandForm brandForm = Helper.createBrandForm("brand 1","category 1");
        int id = brandDto.insertBrand(brandForm);
        BrandForm updatedBrandForm = Helper.createBrandForm("brand 2","category 2");
        brandDto.updateBrand(id,updatedBrandForm);
        BrandPojo brandPojo = brandService.select(id);
        assertEquals(brandPojo.getBrand(),"brand 2");
        assertEquals(brandPojo.getCategory(),"category 2");
    }

    @Test
    public void testUpdateSameData() throws ApiException{
        BrandForm brandForm = Helper.createBrandForm("brand 1","category 1");
        int id = brandDto.insertBrand(brandForm);
        BrandForm updatedBrandForm = Helper.createBrandForm("brand 1","category 1");
        brandDto.updateBrand(id,updatedBrandForm);
        BrandPojo brandPojo = brandService.select(id);
        assertEquals(brandPojo.getBrand(),"brand 1");
        assertEquals(brandPojo.getCategory(),"category 1");
    }

    @Test
    public void testUpdateItemNotExists() throws ApiException{

        BrandForm brandForm = Helper.createBrandForm("brand 1","category 1");
        int id = brandDto.insertBrand(brandForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("brand item doesn't exist!!");
        brandDto.updateBrand(id+1,brandForm);
    }

    @Test
    public void testUpdateDuplicateBrandCategory() throws ApiException{
        BrandForm brandForm = Helper.createBrandForm("brand 1","category 1");
        brandDto.insertBrand(brandForm);
        BrandForm brandForm1 = Helper.createBrandForm("brand 2","category 2");
        int id = brandDto.insertBrand(brandForm1);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("brand-category pair already exist!!");
        brandDto.updateBrand(id,brandForm);
    }

    @Test
    public void testSelectBrand() throws ApiException{
        BrandForm brandForm = Helper.createBrandForm("brand 1","category 1");
        int id = brandDto.insertBrand(brandForm);
        BrandData brandData = brandDto.getBrand(id);
        assertEquals(brandData.getId().intValue(),id);
        assertEquals(brandData.getBrand(),"brand 1");
        assertEquals(brandData.getCategory(),"category 1");
    }

    @Test
    public void testSelectBrandNotExists() throws ApiException{
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("brand item doesn't exist!!");
        brandDto.getBrand(1);
    }

    @Test
    public void testSelectAll() throws ApiException{
        List<BrandData> actualBrandDataList = new ArrayList<BrandData>();
        BrandForm brandForm = Helper.createBrandForm("brand 1","category 1");
        int id = brandDto.insertBrand(brandForm);
        actualBrandDataList.add(Helper.createBrandData(id,"brand 1","category 1"));
        BrandForm brandForm1 = Helper.createBrandForm("brand 2","category 2");
        int id1 = brandDto.insertBrand(brandForm1);
        actualBrandDataList.add(Helper.createBrandData(id1,"brand 2","category 2"));
        List<BrandData>expectedDataList = brandDto.getAllBrand();
        // TODO: should handle this better way
        assertEquals(expectedDataList.size(), actualBrandDataList.size());
    }

}