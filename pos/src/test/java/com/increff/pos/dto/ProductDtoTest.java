package com.increff.pos.dto;

import com.increff.pos.Helper;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.form.ProductUpdateForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.spring.AbstractUnitTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ProductDtoTest extends AbstractUnitTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private BrandDto brandDto;
    @Autowired
    private ProductDto productDto;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void init() throws ApiException {
        BrandForm brandForm = Helper.createBrandForm("brand 1","category 1");
        brandDto.insert(brandForm);
    }

    @Test
    public void TestInsert() throws ApiException{
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        int id = productDto.insert(productForm);
        BrandPojo brandPojo = brandService.getByBrandCategory("brand 1","category 1");
        ProductPojo productPojo = productService.getById(id);
        assertEquals(productPojo.getId().intValue(),id);
        assertEquals(productPojo.getBarcode(),"barcode 1");
        assertEquals(productPojo.getBrandCategoryId(),brandPojo.getId());
        assertEquals(productPojo.getName(),"product 1");
        assertEquals(productPojo.getMrp(),120.12);
    }

    @Test
    public void TestInsertExceptionBrandCategory() throws ApiException{
        ProductForm productForm =  Helper.createProductForm("barcode 1","brand 2","category 2","product 1",120.12);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("The brand-category pair with brand name brand 2 and category name category 2 does not exist");
        productDto.insert(productForm);
    }

    @Test
    public void TestInsertBarcode() throws ApiException{
        ProductForm productForm =  Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        ProductForm duplicateProductForm =  Helper.createProductForm("barcode 1","brand 1","category 1","product 2",120.12);
        productDto.insert(productForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with barcode barcode 1 already exists");
        productDto.insert(duplicateProductForm);
    }

    @Test
    public void TestGetProduct() throws ApiException{
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        int id = productDto.insert(productForm);
        ProductData productData = productDto.getById(id);
        assertEquals(productData.getId().intValue(),id);
        assertEquals(productData.getName(),"product 1");
        assertEquals(productData.getBrand(),"brand 1");
        assertEquals(productData.getCategory(),"category 1");
        assertEquals(productData.getBarcode(),"barcode 1");
        assertEquals(productData.getMrp(),120.12);
    }

    @Test
    public void TestGetProductNotExist() throws ApiException{
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        int id = productDto.insert(productForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product doesn't exist");
        ProductData productData = productDto.getById(id+1);
    }

    @Test
    public void TestGetAllProducts() throws ApiException{
        List<ProductData> actualProductData = new ArrayList<ProductData>();
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        int id = productDto.insert(productForm);
        actualProductData.add(Helper.createProductData(id,productForm));
        ProductForm productForm1 = Helper.createProductForm("barcode 2","brand 1","category 1","product 2",120.12);
        int id1 = productDto.insert(productForm1);
        actualProductData.add(Helper.createProductData(id1,productForm1));
        PaginatedData actualPaginatedData = new PaginatedData(actualProductData, productService.getCount());
        PaginatedData expectedPaginatedData = productDto.getAll(1,10);
        assertEquals(actualPaginatedData.getDataList().size(),expectedPaginatedData.getDataList().size());
    }

    @Test
    public void TestUpdate() throws ApiException{
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        int id = productDto.insert(productForm);
        ProductUpdateForm updatedProductForm = Helper.createProductUpdateForm("product 2",100.00);
        productDto.update(id,updatedProductForm);
        ProductPojo productPojo = productService.getById(id);
        assertEquals(productPojo.getName(),"product 2");
        assertEquals(productPojo.getMrp(),100.00);
    }

    @Test
    public void TestUpdateItemNotExists() throws ApiException{
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        int id = productDto.insert(productForm);
        ProductUpdateForm updatedProductForm = Helper.createProductUpdateForm("product 2",100.00);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product doesn't exist");
        productDto.update(id+1,updatedProductForm);
    }


    @Test
    public void TestBulkInsertError() throws ApiException{
        List<ProductForm>productFormList = new ArrayList<>();
        productFormList.add(Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12));
        productFormList.add(Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12));
        productFormList.add(Helper.createProductForm("barcode 2","brand 2","category 2","product 1",120.12));
        productFormList.add(Helper.createProductForm("barcode 1","brand 1","category 1","product 1",-120.12));
        try{
            productDto.insertList(productFormList);
        } catch (ApiException e) {
            List<ErrorData>actualErrorDataList = new ArrayList<>();
            actualErrorDataList.add(Helper.createErrorData(2,"Product with barcode barcode 1 already exists"));
            actualErrorDataList.add(Helper.createErrorData(3,"Brand item with given name-category doesn't exist"));
            actualErrorDataList.add(Helper.createErrorData(4,"Invalid product Mrp"));
            assertEquals(actualErrorDataList.size(),e.getErrorDataList().size());
        }
    }

    @Test
    public void TestBulkInsert() throws ApiException{
        List<ProductForm>productFormList = new ArrayList<>();
        productFormList.add(Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12));
        productFormList.add(Helper.createProductForm("barcode 2","brand 1","category 1","product 1",120.12));
        productFormList.add(Helper.createProductForm("barcode 3","brand 1","category 1","product 1",120.12));
        productFormList.add(Helper.createProductForm("barcode 4","brand 1","category 1","product 1",120.12));
        productDto.insertList(productFormList);
        List<ProductPojo>expectedProductPojoList = productService.getAll();
        assertEquals(expectedProductPojoList.size() , 4);
    }

}
