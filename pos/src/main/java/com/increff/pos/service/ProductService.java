package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {
    @Autowired
    private ProductDao productDao;

    @Transactional(rollbackOn = ApiException.class)
    public int insert(ProductPojo productPojo) throws ApiException {
        ProductPojo existingProductPojo = productDao.getByBarcode(productPojo.getBarcode());
        if (Objects.nonNull(existingProductPojo)) {
            throw new ApiException("Product with barcode " + productPojo.getBarcode() + " already exists");
        }

        productDao.insert(productPojo);
        return productPojo.getId();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(int id, ProductPojo updatedProductPojo) throws ApiException {
        ProductPojo existingProductPojo = productDao.getById(id);
        ProductPojo productPojoCheck = productDao.getByBarcode(updatedProductPojo.getBarcode());
        if (Objects.nonNull(productPojoCheck) && existingProductPojo != productPojoCheck) {
            throw new ApiException("Product with barcode " + productPojoCheck.getBarcode() + " already exists");
        }

        existingProductPojo.setName(updatedProductPojo.getName());
        existingProductPojo.setMrp(updatedProductPojo.getMrp());
    }


    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo getById(int id) throws ApiException {
        ProductPojo productPojo = productDao.getById(id);
        if (Objects.isNull(productPojo)) {
            throw new ApiException("Product doesn't exist");
        }
        return productPojo;
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo getByBarcode(String barcode) throws ApiException {
        ProductPojo productPojo = productDao.getByBarcode(barcode);
        if (Objects.isNull(productPojo)) {
            throw new ApiException("Product with barcode " + barcode + " doesn't exist");
        }
        return productPojo;
    }

    @Transactional
    public List<ProductPojo> getAll() {
        return productDao.getAll();
    }

    @Transactional
    public List<ProductPojo> getAll(int page, int rowsPerPage) {
        return productDao.getAll((page - 1) * rowsPerPage, rowsPerPage);
    }

    @Transactional
    public List<ProductPojo> getByBrandCategoryId(int brandCategoryId) {
        return productDao.getByBrandCategoryId(brandCategoryId);
    }

    @Transactional
    public Integer getCount() {
        return productDao.getCount();
    }
}
