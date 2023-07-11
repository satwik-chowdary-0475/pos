package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.ErrorData;
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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ProductDto {
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;

    @Transactional(rollbackOn = ApiException.class)
    public int insertProduct(ProductForm form) throws ApiException {
        HelperDto.normalise(form);
        BrandPojo brandPojo = brandService.getBrandByBrandCategory(form.getBrand(),form.getCategory());
        ProductPojo productPojo = HelperDto.convert(form,brandPojo.getId());
        return productService.insertProduct(productPojo);
    }

    //TODO: for loops or streams
    @Transactional(rollbackOn = ApiException.class)
    public void insertProductList(List<ProductForm>productFormList) throws ApiException {
        List<ErrorData> errorDataList = IntStream.range(0, productFormList.size())
                .mapToObj(row -> {
                    ProductForm productForm = productFormList.get(row);
                    try {
                        insertProduct(productForm);
                        return null;
                    } catch (ApiException e) {
                        return HelperDto.convert(row + 1, e.getMessage());
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!errorDataList.isEmpty()) {
            throw new ApiException(errorDataList);
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateProduct(int id, ProductForm form) throws ApiException{
        HelperDto.normalise(form);
        BrandPojo brandPojo = brandService.getBrandByBrandCategory(form.getBrand(),form.getCategory());
        ProductPojo productPojo = HelperDto.convert(form, brandPojo.getId());
        ProductPojo existingProductPojo = productService.getProductByBarcode(productPojo.getBarcode());
        productService.updateProduct(id,productPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductData getProduct(int id) throws ApiException{
        ProductPojo productPojo = productService.getProductById(id);
        BrandPojo brandPojo = brandService.getBrandById(productPojo.getBrandCategory());
        return HelperDto.convert(productPojo,brandPojo.getBrand(),brandPojo.getCategory());
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<ProductData> getAllProducts() throws ApiException{
        List<ProductPojo> productPojoList = productService.getAllProducts();
        List<ProductData> productDataList = new ArrayList<ProductData>();
        for(ProductPojo productPojo : productPojoList){
            BrandPojo brandPojo = brandService.getBrandById(productPojo.getBrandCategory());
            productDataList.add(HelperDto.convert(productPojo,brandPojo.getBrand(), brandPojo.getCategory()));
        }
        return productDataList;
    }
}
