package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.dto.helper.ReportHelperDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.DailySalesData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesData;
import com.increff.pos.model.form.SalesForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.increff.pos.util.StringUtil;
import javafx.util.Pair;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.*;

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

    //TODO: Need to optimise. Wrote brute force O(n^2)
    @Transactional(rollbackOn = ApiException.class)
    public List<InventoryReportData> getInventoryReport() throws ApiException {
        List<BrandPojo> brandPojoList = brandService.selectAll();
        List<InventoryReportData> inventoryReportDataList = new ArrayList<InventoryReportData>();
        for (BrandPojo brandPojo : brandPojoList) {
            InventoryReportData inventoryReportData = new InventoryReportData();
            inventoryReportData.setBrand(brandPojo.getBrand());
            inventoryReportData.setCategory(brandPojo.getCategory());
            inventoryReportData.setQuantity(getInventoryQuantity(brandPojo));
            inventoryReportDataList.add(inventoryReportData);
        }
        return inventoryReportDataList;
    }

    // TODO: Need to handle this better way
    @Transactional(rollbackOn = ApiException.class)
    public int getInventoryQuantity(BrandPojo brandPojo) {
        List<ProductPojo> productPojoList = new ArrayList<ProductPojo>();
        productPojoList.addAll(productService.selectByBrandId(brandPojo.getId()));
        int totalQuantity = 0;
        for (ProductPojo productPojo : productPojoList) {
            try {
                InventoryPojo inventoryPojo = inventoryService.select(productPojo.getId());
                totalQuantity += inventoryPojo.getQuantity();
            } catch (ApiException e) {
                ;
            }
        }
        return totalQuantity;
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<DailySalesData> getDailySalesReport() {
        List<DailySalesReportPojo> dailySalesReportPojoList = dailySalesReportService.selectAll();
        List<DailySalesData> dailySalesData = new ArrayList<DailySalesData>();
        for (DailySalesReportPojo dailySalesReportPojo : dailySalesReportPojoList) {
            dailySalesData.add(ReportHelperDto.convert(dailySalesReportPojo));
        }
        return dailySalesData;
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<BrandData> getBrandCategoryReport() {
        List<BrandPojo> brandPojoList = brandService.selectAll();
        List<BrandData> brandDataList = new ArrayList<BrandData>();
        for (BrandPojo brandPojo : brandPojoList) {
            brandDataList.add(HelperDto.convert(brandPojo));
        }
        return brandDataList;
    }


    //TODO: WROTE BRUTE FORCE n^2 need to optimise. Can we use Pair??
    @Transactional(rollbackOn = ApiException.class)
    public List<SalesData> getSalesReport(SalesForm salesForm) throws ApiException {
        ReportHelperDto.normalise(salesForm);
        Date startTime = Date.valueOf(salesForm.getStartTime().toLocalDate());
        Date endTime = Date.valueOf(salesForm.getEndTime().toLocalDate());
        List<SalesData> salesDataList = new ArrayList<SalesData>();
        List<BrandPojo> brandPojoList = brandService.selectByBrandCategory(salesForm.getBrand(), salesForm.getCategory());
        List<OrderPojo> orderPojoList = orderService.selectByDate(startTime, endTime);
        HashMap<Integer, Pair<Integer, Double>> productOrderItemMap = getProductOrderItemMap(orderPojoList);
        for (BrandPojo brandPojo : brandPojoList) {
            List<ProductPojo> productPojoList = productService.selectByBrandId(brandPojo.getId());
            Integer quantity = new Integer(0);
            Double revenue = new Double(0.0);
            for (ProductPojo productPojo : productPojoList) {
                if (productOrderItemMap.containsKey(productPojo.getId())) {
                    quantity += productOrderItemMap.get(productPojo.getId()).getKey();
                    revenue += productOrderItemMap.get(productPojo.getId()).getValue();
                }
            }
            salesDataList.add(ReportHelperDto.convert(brandPojo, quantity, revenue));
        }

        return salesDataList;

    }

    //TODO : can be optimised??
    public HashMap<Integer, Pair<Integer, Double>> getProductOrderItemMap(List<OrderPojo> orderPojoList) {
        HashMap<Integer, Pair<Integer, Double>> productOrderItemMap = new HashMap<>();
        for (OrderPojo orderPojo : orderPojoList) {
            List<OrderItemPojo> orderItemPojoList = orderItemService.selectAll(orderPojo.getId());
            for (OrderItemPojo orderItemPojo : orderItemPojoList) {
                int productId = orderItemPojo.getProductId();
                if (productOrderItemMap.containsKey(orderItemPojo.getProductId())) {
                    Integer updatedQuantity = productOrderItemMap.get(productId).getKey() + orderItemPojo.getQuantity();
                    Double updatedRevenue = productOrderItemMap.get(productId).getValue() + (orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice());
                    Pair<Integer, Double> updatedEntry = new Pair<>(updatedQuantity, updatedRevenue);
                    productOrderItemMap.put(productId, updatedEntry);
                } else {
                    Integer quantity = orderItemPojo.getQuantity();
                    Double revenue = (orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice());
                    Pair<Integer, Double> updatedEntry = new Pair<>(quantity, revenue);
                    productOrderItemMap.put(productId, updatedEntry);
                }
            }
        }
        return productOrderItemMap;
    }


    // TODO: added just for testing daily sales
    @Transactional(rollbackOn = ApiException.class)
    public void insertDailyReport() {
        dailySalesReportService.insert();
    }

}
