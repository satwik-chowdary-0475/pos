package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.dto.helper.ReportHelperDto;
import com.increff.pos.model.data.*;
import com.increff.pos.model.form.SalesForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import javafx.util.Pair;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
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

    @Transactional(rollbackOn = ApiException.class)
    public List<InventoryReportData> getInventoryReport() throws ApiException {
        List<BrandPojo> brandPojoList = brandService.getAllBrands();
        HashMap<Integer, Integer> inventoryMap = getInventoryMap();
        HashMap<Integer, Integer> brandCategoryMap = getBrandCategoryMap(inventoryMap);
        List<InventoryReportData> inventoryReportDataList = new ArrayList<>();
        for (BrandPojo brandPojo : brandPojoList) {
            int brandId = brandPojo.getId();
            Integer quantity = (brandCategoryMap.containsKey(brandId)) ? brandCategoryMap.get(brandId) : 0;
            inventoryReportDataList.add(ReportHelperDto.convert(brandPojo.getBrand(), brandPojo.getCategory(), quantity));
        }
        return inventoryReportDataList;
    }

    private HashMap<Integer, Integer> getInventoryMap() {
        HashMap<Integer, Integer> inventoryMap = new HashMap<>();
        List<InventoryPojo> inventoryPojoList = inventoryService.getAllProductsInInventory();
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            inventoryMap.put(inventoryPojo.getProductId(), inventoryPojo.getQuantity());
        }
        return inventoryMap;
    }

    private HashMap<Integer, Integer> getBrandCategoryMap(HashMap<Integer, Integer> inventoryMap) {
        List<ProductPojo> productPojoList = productService.getAllProducts();
        HashMap<Integer, Integer> brandCategoryMap = new HashMap<>();
        for (ProductPojo productPojo : productPojoList) {
            if (inventoryMap.containsKey(productPojo.getId())) {
                int brandId = productPojo.getBrandCategory();
                if (brandCategoryMap.containsKey(productPojo.getBrandCategory())) {
                    brandCategoryMap.put(brandId, brandCategoryMap.get(brandId) + inventoryMap.get(productPojo.getId()));
                } else {
                    brandCategoryMap.put(brandId, inventoryMap.get(productPojo.getId()));
                }
            }
        }
        return brandCategoryMap;
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
        List<BrandPojo> brandPojoList = brandService.getAllBrands();
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
        Date startTime = Date.valueOf(LocalDate.parse(salesForm.getStartTime()));
        Date endTime = Date.valueOf(LocalDate.parse(salesForm.getEndTime()));
        List<SalesData> salesDataList = new ArrayList<SalesData>();
        List<BrandPojo> brandPojoList = brandService.getBrandListByBrandCategory(salesForm.getBrand(), salesForm.getCategory());
        List<OrderPojo> orderPojoList = orderService.getOrderByDate(startTime, endTime);
        HashMap<Integer, Pair<Integer, Double>> productOrderItemMap = getProductOrderItemMap(orderPojoList);
        for (BrandPojo brandPojo : brandPojoList) {
            List<ProductPojo> productPojoList = productService.getProductByBrandCategoryId(brandPojo.getId());
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
            List<OrderItemPojo> orderItemPojoList = orderItemService.getAllOrderItems(orderPojo.getId());
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
