package com.increff.pos.dto;

import com.increff.pos.Helper;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.spring.AbstractUnitTest;
import org.junit.Before;
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
        brandDto.insertBrand(brandForm);
    }

    // TODO : TO TEST HELPERS AND NORMALISE CASES
    @Test
    public void TestInsert() throws ApiException{
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        int id = productDto.insertProduct(productForm);
        BrandPojo brandPojo = brandService.getBrandByBrandCategory("brand 1","category 1");
        ProductPojo productPojo = productService.getProductById(id);
        assertEquals(productPojo.getId().intValue(),id);
        assertEquals(productPojo.getBarcode(),"barcode 1");
        assertEquals(productPojo.getBrandCategory(),brandPojo.getId());
        assertEquals(productPojo.getName(),"product 1");
        assertEquals(productPojo.getMrp(),120.12);
    }

    @Test
    public void TestInsertExceptionBrandCategory() throws ApiException{
        ProductForm productForm =  Helper.createProductForm("barcode 1","brand 2","category 2","product 1",120.12);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand item with given name-category doesn't exist!!");
        productDto.insertProduct(productForm);
    }

    @Test
    public void TestInsertBarcode() throws ApiException{
        ProductForm productForm =  Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        ProductForm duplicateProductForm =  Helper.createProductForm("barcode 1","brand 1","category 1","product 2",120.12);
        productDto.insertProduct(productForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with same barcode exists!!");
        productDto.insertProduct(duplicateProductForm);
    }

    @Test
    public void TestGetProduct() throws ApiException{
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        int id = productDto.insertProduct(productForm);
        ProductData productData = productDto.getProduct(id);
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
        int id = productDto.insertProduct(productForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with given id doesn't exist!!");
        ProductData productData = productDto.getProduct(id+1);
    }

    @Test
    public void TestGetAllProducts() throws ApiException{
        List<ProductData> actualProductData = new ArrayList<ProductData>();
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        int id = productDto.insertProduct(productForm);
        actualProductData.add(Helper.createProductData(id,productForm));
        ProductForm productForm1 = Helper.createProductForm("barcode 2","brand 1","category 1","product 2",120.12);
        int id1 = productDto.insertProduct(productForm1);
        actualProductData.add(Helper.createProductData(id1,productForm1));
        List<ProductData>expectedProductData = productDto.getAllProducts();
        assertEquals(expectedProductData.size(),actualProductData.size());
    }

    @Test
    public void TestUpdate() throws ApiException{
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        int id = productDto.insertProduct(productForm);
        ProductForm updatedProductForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 2",100.00);
        productDto.updateProduct(id,updatedProductForm);
        ProductPojo productPojo = productService.getProductByString("barcode 1");
        assertEquals(productPojo.getName(),"product 2");
        assertEquals(productPojo.getMrp(),100.00);
    }

    @Test
    public void TestUpdateItemNotExists() throws ApiException{
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        int id = productDto.insertProduct(productForm);
        ProductForm updatedProductForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 2",100.00);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with same barcode exists!!");
        productDto.updateProduct(id+1,updatedProductForm);
    }

    @Test
    public void TestUpdateBarcode() throws ApiException{
        ProductForm productForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12);
        int id = productDto.insertProduct(productForm);
        ProductForm productForm1 = Helper.createProductForm("barcode 2","brand 1","category 1","product 2",120.12);
        int id1 = productDto.insertProduct(productForm1);
        ProductForm updatedProductForm = Helper.createProductForm("barcode 1","brand 1","category 1","product 2",100.00);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with same barcode exists!!");
        productDto.updateProduct(id1,updatedProductForm);
    }

    @Test
    public void TestBulkInsertError() throws ApiException{
        List<ProductForm>productFormList = new ArrayList<>();
        productFormList.add(Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12));
        productFormList.add(Helper.createProductForm("barcode 1","brand 1","category 1","product 1",120.12));
        productFormList.add(Helper.createProductForm("barcode 2","brand 2","category 2","product 1",120.12));
        productFormList.add(Helper.createProductForm("barcode 1","brand 1","category 1","product 1",-120.12));
        try{
            productDto.insertProductList(productFormList);
        } catch (ApiException e) {
            List<ErrorData>actualErrorDataList = new ArrayList<>();
            actualErrorDataList.add(Helper.createErrorData(2,"Product with same barcode exists!!"));
            actualErrorDataList.add(Helper.createErrorData(3,"Brand item with given name-category doesn't exist!!"));
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
        productDto.insertProductList(productFormList);
        List<ProductPojo>expectedProductPojoList = productService.getAllProducts();
        assertEquals(expectedProductPojoList.size() , 4);
    }

}
