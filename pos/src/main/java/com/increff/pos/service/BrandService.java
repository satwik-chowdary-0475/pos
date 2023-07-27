package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackOn = ApiException.class)
public class BrandService {
    @Autowired
    private BrandDao brandDao;

    public int insert(BrandPojo brandPojo) throws ApiException {
        BrandPojo existingBrandPojo = brandDao.getByBrandCategory(brandPojo.getBrand(), brandPojo.getCategory());
        if (Objects.nonNull(existingBrandPojo))
            throw new ApiException("The brand-category pair " + existingBrandPojo.getBrand() + "-" + existingBrandPojo.getCategory() + " already exists");

        brandDao.insert(brandPojo);
        return brandPojo.getId();
    }

    public void update(int id, BrandPojo brandPojo) throws ApiException {
        BrandPojo existingBrandPojo = getById(id);
        BrandPojo brandPojoCheck = brandDao.getByBrandCategory(brandPojo.getBrand(), brandPojo.getCategory());
        if (Objects.nonNull(brandPojoCheck) && existingBrandPojo != brandPojoCheck)
            throw new ApiException("The brand-category pair " + brandPojo.getBrand() + "-" + brandPojo.getCategory() + " already exists");

        existingBrandPojo.setBrand(brandPojo.getBrand());
        existingBrandPojo.setCategory(brandPojo.getCategory());
    }


    public BrandPojo getById(int id) throws ApiException {
        BrandPojo brandPojo = brandDao.getById(id);
        if (Objects.isNull(brandPojo))
            throw new ApiException("Brand item doesn't exist");

        return brandPojo;
    }

    public BrandPojo getByBrandCategory(String brand, String category) throws ApiException {
        BrandPojo brandPojo = brandDao.getByBrandCategory(brand, category);
        if (Objects.isNull(brandPojo))
            throw new ApiException("The brand-category pair with brand name " + brand + " and category name " + category + " does not exist");

        return brandPojo;
    }

    public List<BrandPojo> getAll(int page, int rowsPerPage) {
        return brandDao.getAll((page - 1) * rowsPerPage, rowsPerPage);
    }

    public Integer getCount() {
        return brandDao.getCount();
    }

    public List<BrandPojo> getAll() {
        return brandDao.getAll();
    }

    public List<BrandPojo> getListByBrandCategory(String brand, String category) {
        boolean brandNotExists = Objects.isNull(brand) || brand.equals("");
        boolean categoryNotExists = Objects.isNull(category) || category.equals("");

        if (brandNotExists && categoryNotExists) {
            return brandDao.getAll();
        }
        if (brandNotExists) {
            return brandDao.getByCategory(category);
        }
        if (categoryNotExists) {
            return brandDao.getByBrand(brand);
        }

        List<BrandPojo> brandPojoList = new ArrayList<>();
        BrandPojo brandPojo = brandDao.getByBrandCategory(brand, category);
        brandPojoList.add(brandPojo);
        return brandPojoList;
    }

}
