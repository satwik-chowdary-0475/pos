package com.increff.pos.controller;


import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.form.ProductUpdateForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductDto productDto;

    @ApiOperation(value = "Inserts a product")
    @PostMapping(path = "")
    public void insertProduct(@RequestBody ProductForm productForm) throws ApiException {
        productDto.insertProduct(productForm);
    }

    @ApiOperation(value = "Update a product")
    @PutMapping(path = "/{id}")
    public void updateProduct(@PathVariable int id, @RequestBody ProductUpdateForm productUpdateForm) throws ApiException {
        productDto.updateProduct(id, productUpdateForm);

    }

    @ApiOperation(value = "Get list of all products")
    @GetMapping(path = "")
    public List<ProductData> getAllProduct() throws ApiException {
        return productDto.getAllProducts();
    }

    @ApiOperation(value = "Get a product details")
    @GetMapping(path = "/{id}")
    public ProductData getProduct(@PathVariable int id) throws ApiException {
        return productDto.getProduct(id);
    }

    @ApiOperation(value = "Add list of products")
    @PostMapping(path = "/bulk")
    public void insertProductList(@RequestBody List<ProductForm> productFormList) throws ApiException {
        productDto.insertProductList(productFormList);
    }


}
