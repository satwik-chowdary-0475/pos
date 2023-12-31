package com.increff.pos.dao;

import com.increff.pos.pojo.BrandPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class BrandDao extends AbstractDao {
    private static final String SELECT_BY_ID = "select p from BrandPojo p where id=:id";
    private static final String SELECT_ALL = "select p from BrandPojo p";
    private static final String SELECT_BY_BRAND_CATEGORY = "select p from BrandPojo p where brand=:brand and category=:category";
    private static final String SELECT_BY_BRAND = "select p from BrandPojo p where brand=:brand";
    private static final String SELECT_BY_CATEGORY = "select p from BrandPojo p where category=:category";
    private static final String SELECT_ALL_COUNT = "select COUNT(p) from BrandPojo p";

    public void insert(BrandPojo brandPojo) {
        em().persist(brandPojo);
    }
    
    public BrandPojo getById(int id) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_BY_ID, BrandPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public BrandPojo getByBrandCategory(String brand, String category) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_BY_BRAND_CATEGORY, BrandPojo.class);
        query.setParameter("brand", brand);
        query.setParameter("category", category);
        return getSingle(query);
    }

    public List<BrandPojo> getByBrand(String brand) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_BY_BRAND, BrandPojo.class);
        query.setParameter("brand", brand);
        return query.getResultList();
    }

    public List<BrandPojo> getByCategory(String category) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_BY_CATEGORY, BrandPojo.class);
        query.setParameter("category", category);
        return query.getResultList();
    }

    
    public List<BrandPojo> getAll(int offset, int rowsPerPage) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_ALL, BrandPojo.class);
        query.setFirstResult(offset);
        query.setMaxResults(rowsPerPage);
        return query.getResultList();
    }

    public List<BrandPojo> getAll() {
        TypedQuery<BrandPojo> query = getQuery(SELECT_ALL, BrandPojo.class);
        return query.getResultList();
    }

    public Integer getCount() {
        TypedQuery<Number> query = getQuery(SELECT_ALL_COUNT, Number.class);
        return query.getSingleResult().intValue();
    }

}
