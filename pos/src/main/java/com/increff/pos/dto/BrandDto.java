package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class BrandDto {
    @Autowired
    private BrandService brandService;

    @Transactional(rollbackOn = ApiException.class)
    public int insertBrand(BrandForm brandForm) throws ApiException {
        HelperDto.normalise(brandForm);
        BrandPojo brandPojo = HelperDto.convert(brandForm);
        return brandService.insert(brandPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateBrand(int id, BrandForm brandForm) throws ApiException{
        HelperDto.normalise(brandForm);
        BrandPojo brandPojo = HelperDto.convert(brandForm);
        brandService.update(id,brandPojo);
    }

    @Transactional
    public List<BrandData> getAllBrand(){
        List<BrandPojo> brandPojoList = brandService.selectAll();
        List<BrandData> brandDataList = new ArrayList<BrandData>();
        for(BrandPojo brandPojo : brandPojoList){
            brandDataList.add(HelperDto.convert(brandPojo));
        }
        return brandDataList;
    }

    @Transactional
    public BrandData getBrand(int id) throws ApiException{
        BrandPojo brandPojo = brandService.select(id);
        return HelperDto.convert(brandPojo);
    }



}
