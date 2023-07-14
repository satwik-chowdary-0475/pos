package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BrandDto {
    @Autowired
    private BrandService brandService;

    @Transactional(rollbackOn = ApiException.class)
    public int insertBrand(BrandForm brandForm) throws ApiException {
        HelperDto.normalise(brandForm);
        BrandPojo brandPojo = HelperDto.convert(brandForm);
        return brandService.insertBrand(brandPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void insertBrandList(List<BrandForm> brandFormList) throws ApiException {
        List<ErrorData> errorDataList = IntStream.range(0, brandFormList.size())
                .mapToObj(row -> {
                    BrandForm brandForm = brandFormList.get(row);
                    try {
                        insertBrand(brandForm);
                        return null;
                    } catch (ApiException e) {
                        return HelperDto.convert(row + 1, e.getMessage());
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!errorDataList.isEmpty()) {
            throw new ApiException(errorDataList);
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateBrand(int id, BrandForm brandForm) throws ApiException {
        HelperDto.normalise(brandForm);
        BrandPojo brandPojo = HelperDto.convert(brandForm);
        brandService.updateBrand(id, brandPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandData getBrandData(int id) throws ApiException {
        BrandPojo brandPojo = brandService.getBrandById(id);
        return HelperDto.convert(brandPojo);
    }

    @Transactional
    public List<BrandData> getAllBrandDataList() {
        List<BrandPojo> brandPojoList = brandService.getAllBrands();
        List<BrandData> brandDataList = brandPojoList.stream()
                .map(HelperDto::convert)
                .collect(Collectors.toList());
        return brandDataList;
    }


}
