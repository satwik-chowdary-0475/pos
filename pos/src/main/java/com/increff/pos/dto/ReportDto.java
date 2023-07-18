package com.increff.pos.dto;

import com.increff.pos.dto.helper.ReportHelperDto;
import com.increff.pos.model.data.*;
import com.increff.pos.model.form.SalesForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j
public class ReportDto {

    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private DailySalesReportService dailySalesReportService;
    @Autowired
    private OrderService orderService;

    @Transactional(rollbackOn = ApiException.class)
    public List<BrandReportData> getBrandCategoryReport() {
        List<BrandPojo> brandPojoList = brandService.getAll();
        List<BrandReportData> brandReportDataList = brandPojoList.stream()
                .map(ReportHelperDto::convert)
                .collect(Collectors.toList());
        return brandReportDataList;
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<DailySalesData> getDailySalesReport() {
        List<DailySalesReportPojo> dailySalesReportPojoList = dailySalesReportService.getAll();
        List<DailySalesData> dailySalesData = dailySalesReportPojoList.stream()
                .map(ReportHelperDto::convert)
                .collect(Collectors.toList());
        return dailySalesData;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void insertDailySalesReport() {
        dailySalesReportService.insert();
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<InventoryReportData> getInventoryReport() throws ApiException {
        List<BrandPojo> brandPojoList = brandService.getAll();
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        HashMap<Integer, Integer> brandCategoryMap = getBrandCategoryMap(inventoryPojoList);

        List<InventoryReportData> inventoryReportDataList = brandPojoList.stream()
                .map(brandPojo -> {
                    int brandId = brandPojo.getId();
                    Integer quantity = brandCategoryMap.getOrDefault(brandId, 0);
                    return ReportHelperDto.convert(brandPojo.getBrand(), brandPojo.getCategory(), quantity);
                })
                .collect(Collectors.toList());

        return inventoryReportDataList;
    }

    private HashMap<Integer, Integer> getBrandCategoryMap(List<InventoryPojo> inventoryPojoList) throws ApiException {
        HashMap<Integer, Integer> brandCategoryMap = new HashMap<>();

        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            ProductPojo productPojo = productService.getProductById(inventoryPojo.getProductId());
            int brandCategoryId = productPojo.getBrandCategoryId();

            if (brandCategoryMap.containsKey(brandCategoryId)) {
                brandCategoryMap.put(brandCategoryId, brandCategoryMap.get(brandCategoryId) + inventoryPojo.getQuantity());
            } else {
                brandCategoryMap.put(brandCategoryId, inventoryPojo.getQuantity());
            }
        }
        return brandCategoryMap;
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<SalesData> getSalesReport(SalesForm salesForm) throws ApiException {
        ReportHelperDto.normalise(salesForm);
        Date startTime = Date.valueOf(LocalDate.parse(salesForm.getStartTime()));
        Date endTime = Date.valueOf(LocalDate.parse(salesForm.getEndTime()));

        List<OrderPojo> orderPojoList = orderService.getByDate(startTime, endTime);
        HashMap<Integer, ReportData> productOrderItemMap = getProductOrderItemMap(orderPojoList);

        List<SalesData> salesDataList = brandService.getListByBrandCategory(salesForm.getBrand(), salesForm.getCategory())
                .stream()
                .filter(Objects::nonNull)
                .map(brandPojo -> {
                    List<ProductPojo> productPojoList = productService.getProductByBrandCategoryId(brandPojo.getId());
                    int quantity = getSalesReportQuantity(productPojoList, productOrderItemMap);
                    double revenue = getSalesReportRevenue(productPojoList, productOrderItemMap);
                    return ReportHelperDto.convert(brandPojo, quantity, revenue);
                })
                .collect(Collectors.toList());

        return salesDataList;

    }

    private double getSalesReportRevenue(List<ProductPojo> productPojoList, HashMap<Integer, ReportData> productOrderItemMap) {
        return productPojoList.stream()
                .filter(productPojo -> productOrderItemMap.containsKey(productPojo.getId()))
                .mapToDouble(productPojo -> productOrderItemMap.get(productPojo.getId()).getRevenue())
                .sum();
    }

    private int getSalesReportQuantity(List<ProductPojo> productPojoList, HashMap<Integer, ReportData> productOrderItemMap) {
        return productPojoList.stream()
                .filter(productPojo -> productOrderItemMap.containsKey(productPojo.getId()))
                .mapToInt(productPojo -> productOrderItemMap.get(productPojo.getId()).getQuantity())
                .sum();
    }

    public HashMap<Integer, ReportData> getProductOrderItemMap(List<OrderPojo> orderPojoList) {
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAllOrderItemsByOrderList(orderPojoList);

        HashMap<Integer, ReportData> productOrderItemMap = orderItemPojoList.stream()
                .collect(Collectors.toMap(
                        OrderItemPojo::getProductId,
                        orderItemPojo -> {
                            Integer quantity = orderItemPojo.getQuantity();
                            Double revenue = orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice();
                            return ReportHelperDto.convert(quantity, revenue);

                        },
                        (existingPair, newPair) -> {
                            Integer updatedQuantity = existingPair.getQuantity() + newPair.getQuantity();
                            Double updatedRevenue = existingPair.getRevenue() + newPair.getRevenue();
                            return ReportHelperDto.convert(updatedQuantity, updatedRevenue);
                        },
                        HashMap::new
                ));

        return productOrderItemMap;
    }


}
