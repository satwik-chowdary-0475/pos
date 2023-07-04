package com.increff.pos.dto.helper;

import com.increff.pos.model.data.*;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.*;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.RandomStrGenerator;
import com.increff.pos.util.RoundUtil;
import com.increff.pos.util.StringUtil;

import java.util.List;
import java.util.Objects;

public class HelperDto {
    public static void validate(ProductForm productForm) throws ApiException {
        if (Objects.isNull(productForm)) {
            throw new ApiException("Invalid input form product details!!!");
        }
        if (Objects.isNull(productForm.getName()) || productForm.getName().length() == 0) {
            throw new ApiException("Invalid product name");
        }
        if (Objects.isNull(productForm.getBarcode()) || (productForm.getBarcode().length() == 0)) {
            throw new ApiException("Invalid product barcode");
        }
        if (Objects.isNull(productForm.getBrand()) || productForm.getBrand().length() == 0) {
            throw new ApiException("Invalid brand name!!");
        }
        if (Objects.isNull(productForm.getCategory()) || productForm.getCategory().length() == 0) {
            throw new ApiException("Invalid brand category!!");
        }
        if (Objects.isNull(productForm.getMrp()) || productForm.getMrp() > 10000000 || productForm.getMrp() <= 0) {
            throw new ApiException("Invalid product Mrp");
        }
        if (productForm.getName().length() > 30) {
            throw new ApiException("Product name cannot be more than 30 characters");
        }
        if (productForm.getBarcode().length() > 30) {
            throw new ApiException("Barcode cannot be more than 30 characters");
        }
        if (productForm.getBrand().length() > 30) {
            throw new ApiException("Brand cannot be more than 30 characters");
        }
        if (productForm.getCategory().length() > 30) {
            throw new ApiException("Category cannot be more than 30 characters");
        }
    }

    public static void validate(InventoryForm inventoryForm) throws ApiException {
        if (Objects.isNull(inventoryForm)) {
            throw new ApiException("Invalid inventory form details");
        }
        if (Objects.isNull(inventoryForm.getBarcode()) || inventoryForm.getBarcode().length() == 0) {
            throw new ApiException("Invalid product barcode!!");
        }
        if (Objects.isNull(inventoryForm.getQuantity()) || inventoryForm.getQuantity() > 1000000 || inventoryForm.getQuantity() < 0) {
            throw new ApiException("Invalid product quantity");
        }
    }


    public static void validate(BrandForm brandForm) throws ApiException {
        if (Objects.isNull(brandForm)) {
            throw new ApiException("Invalid input form brand details!!!");
        }
        if (Objects.isNull(brandForm.getBrand()) || (brandForm.getBrand().length() == 0)) {
            throw new ApiException("Invalid brand name");
        }
        if (Objects.isNull(brandForm.getCategory()) || (brandForm.getCategory().length() == 0)) {
            throw new ApiException("Invalid category name");
        }

        if (brandForm.getBrand().length() > 30) {
            throw new ApiException("Brand name cannot have more than 30 characters");
        }
        if (brandForm.getCategory().length() > 30) {
            throw new ApiException("Category name cannot have more than 30 characters");
        }
    }

    public static void validate(OrderForm orderForm) throws ApiException {
        if (Objects.isNull(orderForm)) {
            throw new ApiException("Invalid order form details");
        }
        if (Objects.isNull(orderForm.getCustomerName()) || orderForm.getCustomerName().length() == 0) {
            throw new ApiException("Invalid order name!!!");
        }
        if (orderForm.getCustomerName().length() > 30) {
            throw new ApiException("Customer name cannot have more than 30 characters");
        }
    }

    public static void validate(OrderItemForm orderItemForm) throws ApiException {
        if (Objects.isNull(orderItemForm)) {
            throw new ApiException("Invalid order item form!!");
        }
        if (Objects.isNull(orderItemForm.getBarcode()) || orderItemForm.getBarcode().length() <= 0) {
            throw new ApiException("Invalid barcode id");
        }
        if (Objects.isNull(orderItemForm.getQuantity()) || orderItemForm.getQuantity() > 1000000 || orderItemForm.getQuantity() <= 0) {
            throw new ApiException("Invalid product quantity");
        }
        if (Objects.isNull(orderItemForm.getSellingPrice()) || orderItemForm.getSellingPrice() <= 0 || orderItemForm.getSellingPrice() > 1000000) {
            throw new ApiException("Invalid selling price");
        }
    }

    public static void validate(UserForm userForm) throws ApiException {
        if (Objects.isNull(userForm)) {
            throw new ApiException("Invalid user form");
        }
        if (Objects.isNull(userForm.getEmail()) || userForm.getEmail().length() == 0) {
            throw new ApiException("Invalid email");
        }
        if (Objects.isNull(userForm.getPassword()) || userForm.getPassword().length() == 0) {
            throw new ApiException("Invalid Password");
        }
        if (Objects.isNull(userForm.getRole()) || userForm.getRole().length() == 0) {
            throw new ApiException("Invalid Role");
        }
    }

    public static OrderDetailsData convert(OrderPojo orderPojo, List<OrderItemData> orderItemDataList) {
        OrderDetailsData orderDetailsData = new OrderDetailsData();
        orderDetailsData.setId(orderPojo.getId());
        orderDetailsData.setStatus(orderPojo.getStatus());
        orderDetailsData.setCustomerName(orderPojo.getCustomerName());
        orderDetailsData.setInvoicedAt(orderPojo.getUpdatedAt());
        orderDetailsData.setCreatedAt(orderPojo.getCreatedAt());
        orderDetailsData.setOrderItems(orderItemDataList.toArray(new OrderItemData[0]));
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
        productPojo.setBrandCategory(brandId);
        return productPojo;
    }

    public static InventoryData convert(InventoryPojo inventoryPojo, String barcode, String productName) throws ApiException {
        InventoryData inventoryData = new InventoryData();
        inventoryData.setProductName(productName);
        inventoryData.setBarcode(barcode);
        inventoryData.setId(inventoryPojo.getId());
        inventoryData.setQuantity(inventoryPojo.getQuantity());
        return inventoryData;
    }

    public static InventoryPojo convert(InventoryForm inventoryForm, int id) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setQuantity(inventoryForm.getQuantity());
        inventoryPojo.setId(id);
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
        orderPojo.setStatus("ACTIVE");
        orderPojo.setOrderCode(RandomStrGenerator.usingUUID(10));
        return orderPojo;
    }

    public static UserPojo convert(UserForm userForm) {
        UserPojo userPojo = new UserPojo();
        userPojo.setEmail(userForm.getEmail());
        userPojo.setRole("standard");
        userPojo.setPassword(userForm.getPassword());
        return userPojo;
    }

    public static OrderData convert(OrderPojo orderPojo) {
        OrderData orderData = new OrderData();
        orderData.setId(orderPojo.getId());
        orderData.setStatus(orderPojo.getStatus());
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

    public static void normalise(ProductForm productForm) throws ApiException {
        productForm.setBrand(StringUtil.toLowerCase(productForm.getBrand()));
        productForm.setCategory(StringUtil.toLowerCase(productForm.getCategory()));
        productForm.setMrp(RoundUtil.round(productForm.getMrp()));
        productForm.setName(StringUtil.toLowerCase(productForm.getName()));
        productForm.setBrand(StringUtil.toLowerCase(productForm.getBrand()));
        productForm.setCategory(StringUtil.toLowerCase(productForm.getCategory()));
        HelperDto.validate(productForm);
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

    public static void normalise(UserForm userForm) throws ApiException {
        userForm.setRole(StringUtil.toLowerCase(userForm.getRole()));
        userForm.setEmail(StringUtil.toLowerCase(userForm.getEmail()));
        HelperDto.validate(userForm);
    }


}
