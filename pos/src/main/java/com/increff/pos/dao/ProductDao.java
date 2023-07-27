package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao {

    private static final String SELECT_BY_ID = "select p from ProductPojo p where id=:id";
    private static final String SELECT_BY_BARCODE = "select p from ProductPojo p where barcode=:barcode";
    private static final String SELECT_ALL = "select p from ProductPojo p";
    private static final String SELECT_BY_BRAND_CATEGORY = "select p from ProductPojo p where brandCategoryId=:brandCategoryId";
    private static final String SELECT_ALL_COUNT = "select COUNT(p) from ProductPojo p";

    public void insert(ProductPojo productPojo) {
        em().persist(productPojo);
    }

    public ProductPojo getById(int id) {
        TypedQuery<ProductPojo> query = getQuery(SELECT_BY_ID, ProductPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<ProductPojo> getByBrandCategoryId(int brandCategoryId) {
        TypedQuery<ProductPojo> query = getQuery(SELECT_BY_BRAND_CATEGORY, ProductPojo.class);
        query.setParameter("brandCategoryId", brandCategoryId);
        return query.getResultList();
    }

    public ProductPojo getByBarcode(String barcode) {
        TypedQuery<ProductPojo> query = getQuery(SELECT_BY_BARCODE, ProductPojo.class);
        query.setParameter("barcode", barcode);
        return getSingle(query);
    }

    public List<ProductPojo> getAll() {
        TypedQuery<ProductPojo> query = getQuery(SELECT_ALL, ProductPojo.class);
        return query.getResultList();
    }

    public List<ProductPojo> getAll(int offset,int rowsPerPage){
        TypedQuery<ProductPojo> query = getQuery(SELECT_ALL, ProductPojo.class);
        query.setFirstResult(offset);
        query.setMaxResults(rowsPerPage);
        return query.getResultList();
    }
    
    public Integer getCount(){
        TypedQuery<Number> query = getQuery(SELECT_ALL_COUNT, Number.class);
        return query.getSingleResult().intValue();
    }


}

