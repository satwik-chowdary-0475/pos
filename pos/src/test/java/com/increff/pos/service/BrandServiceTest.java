package com.increff.pos.service;

import static org.junit.Assert.assertEquals;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.spring.AbstractUnitTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.pojo.BrandPojo;

public class BrandServiceTest extends AbstractUnitTest {

    @Autowired
    private BrandService brandService;
    @Autowired
    private BrandDao brandDao;

    @Test
    public void testAdd() throws ApiException {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand("brand 1");
        brandPojo.setCategory("category 1");
        brandService.insert(brandPojo);
        BrandPojo checkBrandPojo = brandDao.getById(brandPojo.getId());
        assertEquals("brand 1",checkBrandPojo.getBrand());
        assertEquals("category 1",checkBrandPojo.getCategory());
    }

    @Test
    public void testUpdate() throws ApiException {
        BrandPojo brandPojo = new BrandPojo();

    }



}