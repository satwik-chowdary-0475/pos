package com.increff.pos.controller;


import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.constants.PaginationConstant;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {
    @Autowired
    private BrandDto brandDto;

    @ApiOperation(value = "Inserts a brand")
    @PostMapping(path = "")
    public void insertBrand(@RequestBody BrandForm brandForm) throws ApiException {
        brandDto.insert(brandForm);
    }

    @ApiOperation(value = "Updates a brand details")
    @PutMapping(path = "/{id}")
    public void updateBrand(@PathVariable int id, @RequestBody BrandForm brandForm) throws ApiException {
        brandDto.update(id, brandForm);
    }

    @ApiOperation(value = "Get list of all brands")
    @GetMapping(path = "")
    public PaginatedData getAllBrand(
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_ROWS_PER_PAGE) int rowsPerPage) {
        return brandDto.getAll(page, rowsPerPage);
    }

    @ApiOperation(value = "Get a brand details")
    @GetMapping(path = "/{id}")
    public BrandData getBrand(@PathVariable int id) throws ApiException {
        return brandDto.getById(id);
    }

    @ApiOperation(value = "Add list of brand data")
    @PostMapping(path = "/bulk")
    public void insertBrandList(@RequestBody List<BrandForm> brandFormList) throws ApiException {
        brandDto.insertList(brandFormList);
    }

}
