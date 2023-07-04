package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductDto {
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;

    @Transactional(rollbackOn = ApiException.class)
    public int insert(ProductForm form) throws ApiException {
        HelperDto.normalise(form);
        BrandPojo brandPojo = brandService.select(form.getBrand(),form.getCategory());
        ProductPojo productPojo = HelperDto.convert(form,brandPojo.getId());
        return productService.insert(productPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(int id,ProductForm form) throws ApiException{
        HelperDto.normalise(form);
        BrandPojo brandPojo = brandService.select(form.getBrand(),form.getCategory());
        ProductPojo productPojo = HelperDto.convert(form, brandPojo.getId());
        ProductPojo existingProductPojo = productService.select(productPojo.getBarcode());
        productService.update(id,productPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductData getProduct(int id) throws ApiException{
        ProductPojo productPojo = productService.select(id);
        BrandPojo brandPojo = brandService.select(productPojo.getBrandCategory());
        return HelperDto.convert(productPojo,brandPojo.getBrand(),brandPojo.getCategory());
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<ProductData> getAllProducts() throws ApiException{
        List<ProductPojo> productPojoList = productService.selectAll();
        List<ProductData> productDataList = new ArrayList<ProductData>();
        for(ProductPojo productPojo : productPojoList){
            BrandPojo brandPojo = brandService.select(productPojo.getBrandCategory());
            productDataList.add(HelperDto.convert(productPojo,brandPojo.getBrand(), brandPojo.getCategory()));
        }
        return productDataList;
    }



}
