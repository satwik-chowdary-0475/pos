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
    public int insertProduct(ProductPojo productPojo) throws ApiException {
        ProductPojo existingProductPojo = productDao.getProductByBarcode(productPojo.getBarcode());
        if(Objects.nonNull(existingProductPojo)){
            throw new ApiException("Product with same barcode exists!!");
        }
        productDao.insertProduct(productPojo);
        return productPojo.getId();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateProduct(int id, ProductPojo updatedProductPojo) throws ApiException{
        ProductPojo existingProductPojo = productDao.getProductById(id);
        ProductPojo productPojoCheck = productDao.getProductByBarcode(updatedProductPojo.getBarcode());
        if(Objects.nonNull(productPojoCheck) && existingProductPojo != productPojoCheck){
            throw new ApiException("Product with same barcode exists!!");
        }
        existingProductPojo.setBarcode(updatedProductPojo.getBarcode());
        existingProductPojo.setName(updatedProductPojo.getName());
        existingProductPojo.setMrp(updatedProductPojo.getMrp());
        existingProductPojo.setBrandCategory(updatedProductPojo.getBrandCategory());
    }


    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo getProductById(int id) throws ApiException {
        ProductPojo productPojo = productDao.getProductById(id);
        if(Objects.isNull(productPojo)){
            throw new ApiException("Product with given id doesn't exist!!");
        }
        return productPojo;
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo getProductByBarcode(String barcode) throws ApiException {
        ProductPojo productPojo = productDao.getProductByBarcode(barcode);
        if(Objects.isNull(productPojo)){
            throw new ApiException("Product with given barcode doesn't exist!!");
        }
        return productPojo;
    }

    @Transactional
    public List<ProductPojo> getAllProducts(){
        return productDao.getAllProducts();
    }

    @Transactional
    public List<ProductPojo> getProductByBrandCategoryId(int brandCategoryId){
        return productDao.getProductByBrandCategoryId(brandCategoryId);
    }
}
