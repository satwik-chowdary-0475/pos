package com.increff.pos.controller;


import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Api
@RestController
@RequestMapping("/api/brand")
public class BrandController {
    @Autowired
    private BrandDto brandDto;
    @ApiOperation(value = "Inserts a brand")
    @PostMapping(path = "")
    public void insertBrand(@RequestBody BrandForm form) throws ApiException {
        brandDto.insertBrand(form);
    }

    @ApiOperation(value = "Updates a brand details")
    @PutMapping(path = "/{id}")
    public void updateBrand(@PathVariable int id, @RequestBody BrandForm brandForm) throws ApiException{
        brandDto.updateBrand(id,brandForm);
    }

    @ApiOperation(value = "Get list of all brands")
    @GetMapping(path = "")
    public List<BrandData> getAllBrand(){
        return brandDto.getAllBrand();
    }

    @ApiOperation(value = "Get a brand details")
    @GetMapping(path = "/{id}")
    public BrandData getBrand(@PathVariable int id) throws ApiException {
        return brandDto.getBrand(id);
    }



}
