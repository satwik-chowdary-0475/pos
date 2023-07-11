package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao {

    private static String SELECT_BY_ID = "select p from ProductPojo p where id=:id";
    private static String SELECT_BY_BARCODE = "select p from ProductPojo p where barcode=:barcode";
    private static String SELECT_ALL = "select p from ProductPojo p";
    private static String SELECT_BY_BRAND_CATEGORY = "select p from ProductPojo p where brandCategory=:brandCategory";

    @Transactional
    public void insertProduct(ProductPojo productPojo) {
        em().persist(productPojo);
    }

    @Transactional
    public ProductPojo getProductById(int id) {
        TypedQuery<ProductPojo> query = getQuery(SELECT_BY_ID, ProductPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    @Transactional
    public List<ProductPojo> getProductByBrandCategoryId(int brandCategory) {
        TypedQuery<ProductPojo> query = getQuery(SELECT_BY_BRAND_CATEGORY, ProductPojo.class);
        query.setParameter("brandCategory", brandCategory);
        return query.getResultList();
    }

    @Transactional
    public ProductPojo getProductByBarcode(String barcode) {
        TypedQuery<ProductPojo> query = getQuery(SELECT_BY_BARCODE, ProductPojo.class);
        query.setParameter("barcode", barcode);
        return getSingle(query);
    }

    @Transactional
    public List<ProductPojo> getAllProducts() {
        TypedQuery<ProductPojo> query = getQuery(SELECT_ALL, ProductPojo.class);
        return query.getResultList();
    }


}

