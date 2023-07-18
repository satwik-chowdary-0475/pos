package com.increff.pos.dto.helper;

import com.increff.pos.pojo.OrderStatus;
import com.increff.pos.pojo.UserRole;
import com.increff.pos.model.data.*;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.*;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.*;

import java.util.List;
import java.util.Objects;

public class HelperDto {
    public static void validate(ProductForm productForm) throws ApiException {
        if (Objects.isNull(productForm)) {
            throw new ApiException("Invalid input form product details");
        }
        validateStringField(productForm.getName(), "Product name");
        validateStringField(productForm.getBarcode(), "Barcode");
        validateStringField(productForm.getBrand(), "Brand");
        validateStringField(productForm.getCategory(), "Category");
        validateMRP(productForm.getMrp());
    }

    public static void validate(ProductUpdateForm productUpdateForm) throws ApiException {
        if (Objects.isNull(productUpdateForm)) {
            throw new ApiException("Invalid input form product details");
        }
        validateStringField(productUpdateForm.getName(), "Product name");
        validateMRP(productUpdateForm.getMrp());
    }

    public static void validate(InventoryForm inventoryForm) throws ApiException {
        if (Objects.isNull(inventoryForm)) {
            throw new ApiException("Invalid inventory form details");
        }
        validateStringField(inventoryForm.getBarcode(), "Product barcode");
        validateQuantity(inventoryForm.getQuantity(), false);
    }

    public static void validate(InventoryUpdateForm inventoryUpdateForm) throws ApiException {
        if (Objects.isNull(inventoryUpdateForm)) {
            throw new ApiException("Invalid inventory form details");
        }
        validateQuantity(inventoryUpdateForm.getQuantity(), true);
    }

    public static void validate(BrandForm brandForm) throws ApiException {
        if (Objects.isNull(brandForm)) {
            throw new ApiException("Invalid input form brand details");
        }
        validateStringField(brandForm.getBrand(), "Brand name");
        validateStringField(brandForm.getCategory(), "Category name");
    }

    public static void validate(OrderForm orderForm) throws ApiException {
        if (Objects.isNull(orderForm)) {
            throw new ApiException("Invalid order form details");
        }
        validateStringField(orderForm.getCustomerName(), "Customer name");
    }

    public static void validate(OrderItemForm orderItemForm) throws ApiException {
        if (Objects.isNull(orderItemForm)) {
            throw new ApiException("Invalid order item form");
        }
        validateStringField(orderItemForm.getBarcode(), "Barcode");
        validateQuantity(orderItemForm.getQuantity(), true);
        validateSellingPrice(orderItemForm.getSellingPrice());
    }

    public static void validate(OrderItemUpdateForm orderItemUpdateForm) throws ApiException {
        if (Objects.isNull(orderItemUpdateForm)) {
            throw new ApiException("Invalid order item form");
        }
        validateQuantity(orderItemUpdateForm.getQuantity(), true);
        validateSellingPrice(orderItemUpdateForm.getSellingPrice());
    }

    public static void validate(UserForm userForm) throws ApiException {
        if (Objects.isNull(userForm)) {
            throw new ApiException("Invalid user form");
        }
        validateStringField(userForm.getEmail(), "Email");
        validateStringField(userForm.getPassword(), "Password");
    }

    private static void validateStringField(String field, String fieldName) throws ApiException {
        if (Objects.isNull(field) || field.trim().isEmpty()) {
            throw new ApiException("Invalid " + fieldName.toLowerCase() + ". " + fieldName + " is required.");
        }
        if (field.length() > 30) {
            throw new ApiException(fieldName + " cannot be more than 30 characters.");
        }
    }

    private static void validateMRP(Double mrp) throws ApiException {
        if (Objects.isNull(mrp) || mrp <= 0 || mrp > 10000000) {
            throw new ApiException("Invalid product MRP.");
        }
    }

    private static void validateSellingPrice(Double sellingPrice) throws ApiException {
        if (Objects.isNull(sellingPrice) || sellingPrice <= 0 || sellingPrice > 10000000) {
            throw new ApiException("Invalid product selling price.");
        }
    }

    private static void validateQuantity(Integer quantity, boolean allowZero) throws ApiException {
        if (Objects.isNull(quantity) || (quantity < 0 && !allowZero) || quantity > 10000000) {
            throw new ApiException("Invalid product quantity.");
        }
    }

    public static OrderDetailsData convert(OrderPojo orderPojo, List<OrderItemData> orderItemDataList) {
        OrderDetailsData orderDetailsData = new OrderDetailsData();
        orderDetailsData.setId(orderPojo.getId());
        orderDetailsData.setStatus(orderPojo.getStatus().name());
        orderDetailsData.setCustomerName(orderPojo.getCustomerName());
        orderDetailsData.setInvoicedAt(orderPojo.getUpdatedAt());
        orderDetailsData.setCreatedAt(orderPojo.getCreatedAt());
        orderDetailsData.setOrderItems(orderItemDataList);
        orderDetailsData.setOrderCode(orderPojo.getOrderCode());
        return orderDetailsData;
    }

    public static ProductData convert(ProductPojo productPojo, String brand, String category) {
        ProductData productData = new ProductData();
        productData.setId(productPojo.getId());
        productData.setBrand(brand);
        productData.setCategory(category);
        productData.setBarcode(productPojo.getBarcode());
        productData.setMrp(productPojo.getMrp());
        productData.setName(productPojo.getName());
        return productData;
    }

    public static ProductPojo convert(ProductForm productForm, int brandId) {
        ProductPojo productPojo = new ProductPojo();
        productPojo.setBarcode(productForm.getBarcode());
        productPojo.setMrp(productForm.getMrp());
        productPojo.setName(productForm.getName());
        productPojo.setBrandCategoryId(brandId);
        return productPojo;
    }

    public static ProductPojo convert(ProductUpdateForm productUpdateForm) {
        ProductPojo productPojo = new ProductPojo();
        productPojo.setMrp(productUpdateForm.getMrp());
        productPojo.setName(productUpdateForm.getName());
        return productPojo;
    }

    public static InventoryData convert(InventoryPojo inventoryPojo, String barcode, String productName) throws ApiException {
        InventoryData inventoryData = new InventoryData();
        inventoryData.setProductName(productName);
        inventoryData.setBarcode(barcode);
        inventoryData.setProductId(inventoryPojo.getProductId());
        inventoryData.setQuantity(inventoryPojo.getQuantity());
        return inventoryData;
    }

    public static InventoryPojo convert(InventoryForm inventoryForm, int productId) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setQuantity(inventoryForm.getQuantity());
        inventoryPojo.setProductId(productId);
        return inventoryPojo;
    }

    public static InventoryPojo convert(InventoryUpdateForm inventoryUpdateForm, Integer productId) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setQuantity(inventoryUpdateForm.getQuantity());
        inventoryPojo.setProductId(productId);
        return inventoryPojo;
    }

    public static BrandData convert(BrandPojo brandPojo) {
        BrandData brandData = new BrandData();
        brandData.setId(brandPojo.getId());
        brandData.setBrand(brandPojo.getBrand());
        brandData.setCategory(brandPojo.getCategory());
        return brandData;
    }

    public static BrandPojo convert(BrandForm brandForm) {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(brandForm.getBrand());
        brandPojo.setCategory(brandForm.getCategory());
        return brandPojo;
    }

    public static OrderPojo convert(OrderForm orderForm) {
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setCustomerName(orderForm.getCustomerName());
        orderPojo.setStatus(OrderStatus.CREATED);
        orderPojo.setOrderCode(RandomStrGenerator.usingUUID(10));
        return orderPojo;
    }

    public static UserPojo convert(UserForm userForm) {
        UserPojo userPojo = new UserPojo();
        userPojo.setEmail(userForm.getEmail());
        userPojo.setPassword(userForm.getPassword());
        if (AdminUtil.checkAdmin(userForm.getEmail())) {
            userPojo.setRole(UserRole.SUPERVISOR);
        } else {
            userPojo.setRole(UserRole.OPERATOR);
        }
        return userPojo;
    }

    public static OrderData convert(OrderPojo orderPojo) {
        OrderData orderData = new OrderData();
        orderData.setId(orderPojo.getId());
        orderData.setStatus(orderPojo.getStatus().name());
        orderData.setCustomerName(orderPojo.getCustomerName());
        orderData.setCreatedAt(orderPojo.getCreatedAt());
        orderData.setUpdatedAt(orderPojo.getUpdatedAt());
        orderData.setOrderCode(orderPojo.getOrderCode());
        return orderData;
    }

    public static OrderItemPojo convert(OrderItemForm orderItemForm, int orderId, ProductPojo productPojo) {
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setOrderId(orderId);
        orderItemPojo.setProductId(productPojo.getId());
        orderItemPojo.setSellingPrice(orderItemForm.getSellingPrice());
        orderItemPojo.setQuantity(orderItemForm.getQuantity());
        return orderItemPojo;
    }

    public static OrderItemPojo convert(OrderItemUpdateForm orderItemUpdateForm, int orderId) {
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setOrderId(orderId);
        orderItemPojo.setSellingPrice(orderItemUpdateForm.getSellingPrice());
        orderItemPojo.setQuantity(orderItemUpdateForm.getQuantity());
        return orderItemPojo;
    }

    public static OrderItemData convert(OrderItemPojo orderItemPojo, String barcode, String productName) {
        OrderItemData orderItemData = new OrderItemData();
        orderItemData.setId(orderItemPojo.getId());
        orderItemData.setOrderId(orderItemPojo.getOrderId());
        orderItemData.setProductName(productName);
        orderItemData.setQuantity(orderItemPojo.getQuantity());
        orderItemData.setSellingPrice(orderItemPojo.getSellingPrice());
        orderItemData.setBarcode(barcode);
        orderItemData.setTotalPrice(orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice());
        return orderItemData;
    }


    public static ErrorData convert(int row, String errorMessage) {
        ErrorData errorData = new ErrorData();
        errorData.setErrorMessage(errorMessage);
        errorData.setRow(row);
        return errorData;
    }

    public static void normalise(ProductForm productForm) throws ApiException {
        productForm.setBrand(StringUtil.toLowerCase(productForm.getBrand()));
        productForm.setCategory(StringUtil.toLowerCase(productForm.getCategory()));
        productForm.setMrp(RoundUtil.round(productForm.getMrp()));
        productForm.setName(StringUtil.toLowerCase(productForm.getName()));
        HelperDto.validate(productForm);
    }

    public static void normalise(ProductUpdateForm productUpdateForm) throws ApiException {
        productUpdateForm.setName(StringUtil.toLowerCase(productUpdateForm.getName()));
        productUpdateForm.setMrp(RoundUtil.round(productUpdateForm.getMrp()));
        HelperDto.validate(productUpdateForm);
    }


    public static void normalise(BrandForm brandForm) throws ApiException {
        brandForm.setBrand(StringUtil.toLowerCase(brandForm.getBrand()));
        brandForm.setCategory(StringUtil.toLowerCase(brandForm.getCategory()));
        HelperDto.validate(brandForm);
    }

    public static void normalise(OrderForm orderForm) throws ApiException {
        orderForm.setCustomerName(StringUtil.toLowerCase(orderForm.getCustomerName()));
        HelperDto.validate(orderForm);
    }

    public static void normalise(OrderItemForm orderItemForm) throws ApiException {
        orderItemForm.setSellingPrice(RoundUtil.round(orderItemForm.getSellingPrice()));
        HelperDto.validate(orderItemForm);
    }

    public static void normalise(OrderItemUpdateForm orderItemUpdateForm) throws ApiException {
        orderItemUpdateForm.setSellingPrice(RoundUtil.round(orderItemUpdateForm.getSellingPrice()));
        HelperDto.validate(orderItemUpdateForm);
    }

    public static void normalise(UserForm userForm) throws ApiException {
        userForm.setRole(StringUtil.toLowerCase(userForm.getRole()));
        userForm.setEmail(StringUtil.toLowerCase(userForm.getEmail()));
        HelperDto.validate(userForm);
    }
}
