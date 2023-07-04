package com.increff.pos.dao;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao {

    private static String SELECT_BY_ID = "select p from ProductPojo p where id=:id";
    private static String SELECT_BY_BARCODE = "select p from ProductPojo p where barcode=:barcode";
    private static String SELECT_ALL = "select p from ProductPojo p";
    private static String SELECT_BY_BRAND = "select p from ProductPojo p where brandCategory=:brandCategory";

    @Transactional
    public void insert(ProductPojo productPojo) {
        em().persist(productPojo);
    }

    @Transactional
    public ProductPojo select(int id) {
        TypedQuery<ProductPojo> query = getQuery(SELECT_BY_ID, ProductPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    @Transactional
    public List<ProductPojo> selectByBrandId(int brandCategory) {
        TypedQuery<ProductPojo> query = getQuery(SELECT_BY_BRAND, ProductPojo.class);
        query.setParameter("brandCategory", brandCategory);
        return query.getResultList();
    }

    @Transactional
    public ProductPojo select(String barcode) {
        TypedQuery<ProductPojo> query = getQuery(SELECT_BY_BARCODE, ProductPojo.class);
        query.setParameter("barcode", barcode);
        return getSingle(query);
    }

    @Transactional
    public List<ProductPojo> selectAll() {
        TypedQuery<ProductPojo> query = getQuery(SELECT_ALL, ProductPojo.class);
        return query.getResultList();
    }


}

