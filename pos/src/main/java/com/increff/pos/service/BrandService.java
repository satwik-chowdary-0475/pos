package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Log4j
public class BrandService {
    @Autowired
    private BrandDao brandDao;

    @Transactional(rollbackOn = ApiException.class)
    public int insert(BrandPojo newBrandPojo) throws ApiException {
        BrandPojo existingBrandPojo = brandDao.select(newBrandPojo.getBrand(), newBrandPojo.getCategory());
        if (Objects.nonNull(existingBrandPojo))
            throw new ApiException("brand-category pair already exist!!");
        brandDao.insert(newBrandPojo);
        return newBrandPojo.getId();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(int id, BrandPojo newBrandPojo) throws ApiException {
        BrandPojo existingBrandPojo = brandDao.select(id);
        if (Objects.isNull(existingBrandPojo))
            throw new ApiException("brand item doesn't exist!!");
        BrandPojo brandPojoCheck = brandDao.select(newBrandPojo.getBrand(), newBrandPojo.getCategory());
        if (Objects.nonNull(brandPojoCheck) && existingBrandPojo != brandPojoCheck)
            throw new ApiException("brand-category pair already exist!!");
        existingBrandPojo.setBrand(newBrandPojo.getBrand());
        existingBrandPojo.setCategory(newBrandPojo.getCategory());
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo select(int id) throws ApiException {
        BrandPojo brandPojo = brandDao.select(id);
        if (Objects.isNull(brandPojo))
            throw new ApiException("brand item doesn't exist!!");
        return brandPojo;
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo select(String brand, String category) throws ApiException {
        BrandPojo brandPojo = brandDao.select(brand, category);
        if (Objects.isNull(brandPojo))
            throw new ApiException("brand item with given name-category doesn't exist!!");
        return brandPojo;
    }

    @Transactional
    public List<BrandPojo> selectAll() {
        return brandDao.selectAll();
    }


    @Transactional(rollbackOn = ApiException.class)
    public List<BrandPojo> selectByBrandCategory(String brand, String category) {
        if (brand.equals("") && category.equals("")) {
            return brandDao.selectAll();
        }
        if (brand.equals("")) {
            return brandDao.selectByCategory(category);
        }
        if (category.equals("")) {
            return brandDao.selectByBrand(brand);
        }
        List<BrandPojo> brandPojoList = new ArrayList<BrandPojo>();
        BrandPojo brandPojo = brandDao.select(brand, category);
        brandPojoList.add(brandPojo);
        return brandPojoList;
    }

}
