package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;
import lombok.extern.log4j.Log4j;
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
    public int insertBrand(BrandPojo brandPojo) throws ApiException {
        BrandPojo existingBrandPojo = brandDao.getBrandByBrandCategory(brandPojo.getBrand(), brandPojo.getCategory());
        if (Objects.nonNull(existingBrandPojo))
            throw new ApiException("Brand-category pair already exist!!");
        brandDao.insertBrand(brandPojo);
        return brandPojo.getId();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateBrand(int id, BrandPojo brandPojo) throws ApiException {
        BrandPojo existingBrandPojo = getBrandById(id);
        BrandPojo brandPojoCheck = brandDao.getBrandByBrandCategory(brandPojo.getBrand(), brandPojo.getCategory());
        if (Objects.nonNull(brandPojoCheck) && existingBrandPojo != brandPojoCheck)
            throw new ApiException("Brand-category pair already exist!!");
        existingBrandPojo.setBrand(brandPojo.getBrand());
        existingBrandPojo.setCategory(brandPojo.getCategory());
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo getBrandById(int id) throws ApiException {
        BrandPojo brandPojo = brandDao.getBrandById(id);
        if (Objects.isNull(brandPojo))
            throw new ApiException("Brand item doesn't exist!!");
        return brandPojo;
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo getBrandByBrandCategory(String brand, String category) throws ApiException {
        BrandPojo brandPojo = brandDao.getBrandByBrandCategory(brand, category);
        if (Objects.isNull(brandPojo))
            throw new ApiException("Brand item with given name-category doesn't exist!!");
        return brandPojo;
    }

    @Transactional
    public List<BrandPojo> getAllBrands() {
        return brandDao.getAllBrands();
    }


    // TODO : need to change here
    @Transactional(rollbackOn = ApiException.class)
    public List<BrandPojo> getBrandListByBrandCategory(String brand, String category) {
        boolean brandExists = Objects.isNull(brand) || brand.equals("");
        boolean categoryExists = Objects.isNull(category) || category.equals("");
        if (brandExists && categoryExists) {
            return brandDao.getAllBrands();
        }
        if (brandExists) {
            return brandDao.getBrandByCategory(category);
        }
        if (categoryExists) {
            return brandDao.getBrandByBrand(brand);
        }
        List<BrandPojo> brandPojoList = new ArrayList<BrandPojo>();
        BrandPojo brandPojo = brandDao.getBrandByBrandCategory(brand, category);
        brandPojoList.add(brandPojo);
        return brandPojoList;
    }

}
