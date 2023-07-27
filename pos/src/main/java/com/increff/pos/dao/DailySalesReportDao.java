package com.increff.pos.dao;

import com.increff.pos.pojo.DailySalesReportPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class DailySalesReportDao extends AbstractDao {
    private static final String SELECT_ALL = "select p from DailySalesReportPojo p";

    public void insert(DailySalesReportPojo dailySalesReportPojo) {
        em().persist(dailySalesReportPojo);
    }

    public List<DailySalesReportPojo> getAll() {
        TypedQuery<DailySalesReportPojo> query = getQuery(SELECT_ALL, DailySalesReportPojo.class);
        return query.getResultList();
    }
}
