package com.increff.pos.controller;


import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductDto productDto;

    @ApiOperation(value = "Inserts a product")
    @PostMapping(path = "/product")
    public void add(@RequestBody ProductForm form) throws ApiException {
        productDto.insert(form);
    }

    @ApiOperation(value = "Update a product")
    @PutMapping(path = "/product/{id}")
    public void update(@PathVariable int id,@RequestBody ProductForm form) throws ApiException{
        productDto.update(id,form);

    }

    @ApiOperation(value = "Get list of all products")
    @GetMapping(path = "/product")
    public List<ProductData> getAllProduct() throws ApiException {
        return productDto.getAllProducts();
    }

    @ApiOperation(value = "Get a product details")
    @GetMapping(path = "/product/{id}")
    public ProductData getProduct(@PathVariable int id) throws ApiException {
        return productDto.getProduct(id);
    }


}
