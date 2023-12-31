package com.increff.pos;

import com.increff.pos.model.data.*;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.OrderPojo;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Helper {

    public static UserForm createUserForm(String email,String password){
        UserForm userForm = new UserForm();
        userForm.setEmail(email);
        userForm.setPassword(password);
        return userForm;
    }

    public static BrandForm createBrandForm(String brand, String category){
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand(brand);
        brandForm.setCategory(category);
        return brandForm;
    }

    public static ProductUpdateForm createProductUpdateForm(String name,Double mrp){
        ProductUpdateForm productUpdateForm = new ProductUpdateForm();
        productUpdateForm.setName(name);
        productUpdateForm.setMrp(mrp);
        return productUpdateForm;
    }

    public static BrandData createBrandData(int id, String brand, String category) {
        BrandData brandData = new BrandData();
        brandData.setBrand(brand);
        brandData.setCategory(category);
        brandData.setId(id);
        return brandData;
    }

    public static ProductForm createProductForm(String barcode,String brand,String category,String name,Double mrp) {
        ProductForm productForm = new ProductForm();
        productForm.setBarcode(barcode);
        productForm.setMrp(mrp);
        productForm.setName(name);
        productForm.setBrand(brand);
        productForm.setCategory(category);
        return productForm;
    }

    public static ProductData createProductData(int id,ProductForm productForm) {
        ProductData productData = new ProductData();
        productData.setId(id);
        productData.setBarcode(productForm.getBarcode());
        productData.setMrp(productForm.getMrp());
        productData.setName(productForm.getName());
        productData.setBrand(productForm.getBrand());
        productData.setCategory(productForm.getCategory());
        return productData;
    }

    public static InventoryForm createInventoryForm(String barcode,int quantity) {
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(quantity);
        return inventoryForm;
    }

    public static InventoryData createInventoryData(int id,String productName,String barcode,int quantity){
        InventoryData inventoryData = new InventoryData();
        inventoryData.setProductName(productName);
        inventoryData.setProductId(id);
        inventoryData.setQuantity(quantity);
        inventoryData.setBarcode(barcode);
        return inventoryData;
    }

    public static OrderForm createOrderForm(String customerName) {
        OrderForm orderForm = new OrderForm();
        orderForm.setCustomerName(customerName);
        return orderForm;
    }

    public static OrderData createOrderData(OrderPojo orderPojo) {
        OrderData orderData = new OrderData();
        orderData.setOrderCode(orderPojo.getOrderCode());
        orderData.setStatus(orderPojo.getStatus().name());
        orderData.setCreatedAt(orderPojo.getCreatedAt());
        orderData.setUpdatedAt(orderPojo.getUpdatedAt());
        orderData.setId(orderPojo.getId());
        orderData.setCustomerName(orderPojo.getCustomerName());
        return orderData;
    }

    public static OrderDetailsData createOrderDetailsData(OrderPojo orderPojo, List<OrderItemData> orderItemDataList) {
        OrderDetailsData orderDetailsData = new OrderDetailsData();
        orderDetailsData.setId(orderPojo.getId());
        orderDetailsData.setOrderItems(orderItemDataList);
        orderDetailsData.setCreatedAt(orderPojo.getCreatedAt());
        orderDetailsData.setInvoicedAt(orderPojo.getUpdatedAt());
        orderDetailsData.setStatus(orderPojo.getStatus().name());
        orderDetailsData.setCustomerName(orderDetailsData.getCustomerName());
        orderDetailsData.setStatus(orderPojo.getStatus().name());
        return orderDetailsData;
    }

    public static OrderItemForm createOrderItemForm(String barcode,Integer quantity,Double sellingPrice) {
        OrderItemForm orderItemForm = new OrderItemForm();
        orderItemForm.setSellingPrice(sellingPrice);
        orderItemForm.setBarcode(barcode);
        orderItemForm.setQuantity(quantity);
        return orderItemForm;
    }

    public static SalesForm createSalesForm(String brand, String category, String startTime,String endTime) {
        SalesForm salesForm = new SalesForm();
        salesForm.setBrand(brand);
        salesForm.setCategory(category);
        salesForm.setStartTime(startTime);
        salesForm.setEndTime(endTime);
        return salesForm;
    }

    public static ErrorData createErrorData(int row, String errorMessage) {
        ErrorData errorData = new ErrorData();
        errorData.setRow(row);
        errorData.setErrorMessage(errorMessage);
        return errorData;
    }

    public static InventoryUpdateForm createInventoryUpdateForm(int quantity) {
        InventoryUpdateForm inventoryUpdateForm = new InventoryUpdateForm();
        inventoryUpdateForm.setQuantity(quantity);
        return inventoryUpdateForm;
    }

    public static OrderItemUpdateForm createOrderItemUpdateForm(int quantity, Double sellingPrice) {
        OrderItemUpdateForm orderItemUpdateForm = new OrderItemUpdateForm();
        orderItemUpdateForm.setSellingPrice(sellingPrice);
        orderItemUpdateForm.setQuantity(quantity);
        return orderItemUpdateForm;
    }
}
