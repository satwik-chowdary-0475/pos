package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.form.ProductUpdateForm;
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
    public int insert(ProductForm form) throws ApiException {
        HelperDto.normalise(form);
        BrandPojo brandPojo = brandService.getByBrandCategory(form.getBrand(),form.getCategory());
        ProductPojo productPojo = HelperDto.convert(form,brandPojo.getId());
        return productService.insert(productPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(int id, ProductUpdateForm productUpdateForm) throws ApiException{
        HelperDto.normalise(productUpdateForm);
        ProductPojo productPojo = HelperDto.convert(productUpdateForm);
        productService.getById(id);
        productService.update(id,productPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductData getById(int id) throws ApiException{
        ProductPojo productPojo = productService.getById(id);
        BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategoryId());
        return HelperDto.convert(productPojo,brandPojo.getBrand(),brandPojo.getCategory());
    }

    @Transactional(rollbackOn = ApiException.class)
    public PaginatedData getAll(int page,int rowsPerPage) throws ApiException{
        List<ProductPojo> productPojoList = productService.getAll(page,rowsPerPage);
        Integer totalCount = productService.getCount();

        List<ProductData> productDataList = new ArrayList<ProductData>();
        for(ProductPojo productPojo : productPojoList){
            BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategoryId());
            productDataList.add(HelperDto.convert(productPojo,brandPojo.getBrand(), brandPojo.getCategory()));
        }

        return new PaginatedData(productDataList,totalCount);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void insertList(List<ProductForm>productFormList) throws ApiException {
        List<ErrorData> errorDataList = IntStream.range(0, productFormList.size())
                .mapToObj(row -> {
                    ProductForm productForm = productFormList.get(row);
                    try {
                        insert(productForm);
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
}
