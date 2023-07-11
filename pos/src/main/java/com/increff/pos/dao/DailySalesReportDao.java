package com.increff.pos.dao;

import com.increff.pos.pojo.DailySalesReportPojo;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Log4j
public class DailySalesReportDao extends AbstractDao {
    private static String SELECT_ALL = "select p from DailySalesReportPojo p";

    @Transactional
    public void insertDailySalesReport(DailySalesReportPojo dailySalesReportPojo) {
        em().persist(dailySalesReportPojo);
    }

    public List<DailySalesReportPojo> getAllDailySalesReport() {
        TypedQuery<DailySalesReportPojo> query = getQuery(SELECT_ALL, DailySalesReportPojo.class);
        return query.getResultList();
    }
}
