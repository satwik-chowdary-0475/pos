package com.increff.pos.dao;


import com.increff.pos.pojo.BrandPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class BrandDao extends AbstractDao {
    private static String SELECT_BY_ID = "select p from BrandPojo p where id=:id";
    private static String SELECT_ALL = "select p from BrandPojo p";
    private static String SELECT_BY_BRAND_CATEGORY = "select p from BrandPojo p where brand=:brand and category=:category";
    private static String SELECT_BY_BRAND = "select p from BrandPojo p where brand=:brand";
    private static String SELECT_BY_CATEGORY = "select p from BrandPojo p where category=:category";

    @Transactional
    public void insert(BrandPojo brandPojo) {
        em().persist(brandPojo);
    }

    @Transactional
    public BrandPojo getById(int id) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_BY_ID, BrandPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    @Transactional
    public BrandPojo getByBrandCategory(String brand, String category) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_BY_BRAND_CATEGORY, BrandPojo.class);
        query.setParameter("brand", brand);
        query.setParameter("category", category);
        return getSingle(query);
    }

    @Transactional
    public List<BrandPojo> getByBrand(String brand) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_BY_BRAND, BrandPojo.class);
        query.setParameter("brand", brand);
        return query.getResultList();
    }

    @Transactional
    public List<BrandPojo> getByCategory(String category) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_BY_CATEGORY, BrandPojo.class);
        query.setParameter("category", category);
        return query.getResultList();
    }

    @Transactional
    public List<BrandPojo> getAll() {
        TypedQuery<BrandPojo> query = getQuery(SELECT_ALL, BrandPojo.class);
        return query.getResultList();
    }


}
