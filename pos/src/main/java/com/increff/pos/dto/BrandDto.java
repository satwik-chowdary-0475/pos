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
    public int insert(BrandForm brandForm) throws ApiException {
        HelperDto.normalise(brandForm);
        BrandPojo brandPojo = HelperDto.convert(brandForm);
        return brandService.insert(brandPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(int id, BrandForm brandForm) throws ApiException {
        HelperDto.normalise(brandForm);
        BrandPojo brandPojo = HelperDto.convert(brandForm);
        brandService.update(id, brandPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandData getById(int id) throws ApiException {
        BrandPojo brandPojo = brandService.getById(id);
        return HelperDto.convert(brandPojo);
    }

    @Transactional
    public List<BrandData> getAll() {
        List<BrandPojo> brandPojoList = brandService.getAll();

        List<BrandData> brandDataList = brandPojoList.stream()
                .map(HelperDto::convert)
                .collect(Collectors.toList());
        return brandDataList;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void insertList(List<BrandForm> brandFormList) throws ApiException {
        List<ErrorData> errorDataList = IntStream.range(0, brandFormList.size())
                .mapToObj(row -> {
                    BrandForm brandForm = brandFormList.get(row);
                    try {
                        insert(brandForm);
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

}
