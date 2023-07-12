package com.increff.pos.service;

import com.increff.pos.dao.DailySalesReportDao;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.DailySalesReportPojo;
import com.increff.pos.pojo.OrderPojo;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Log4j
public class DailySalesReportService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private DailySalesReportDao dailySalesReportDao;

    @Transactional
    public void insertDailySalesReport(){
        LocalDate currentDate = LocalDate.now();
        Date currentDay = Date.valueOf(currentDate);
        LocalDate previousDate = currentDate.minusDays(1);
        Date previousDay = Date.valueOf(previousDate);
        List<OrderPojo> orderPojoList = orderDao.getOrderByDate(previousDay,currentDay);
        Object[] dailySalesReport = getOrderItemsReport(orderPojoList);
        Integer totalInvoicedItems = (Integer)(dailySalesReport[0]);
        Double totalRevenue = ((Double) dailySalesReport[1]);
        DailySalesReportPojo dailySalesReportPojo = setDailySalesReport(totalInvoicedItems,totalRevenue,orderPojoList.size());
        dailySalesReportDao.insertDailySalesReport(dailySalesReportPojo);
    }

    private Object[] getOrderItemsReport(List<OrderPojo>orderPojoList){
        if(!orderPojoList.isEmpty()){
            orderItemDao.getOrderItemsReport(orderPojoList);
        }
        return new Object[]{0,0.0};
    }
    public DailySalesReportPojo setDailySalesReport(Integer totalInvoicedItems,Double totalRevenue, int ordersCount){
        DailySalesReportPojo dailySalesReportPojo = new DailySalesReportPojo();
        dailySalesReportPojo.setInvoicedItemsCount((Integer) totalInvoicedItems);
        dailySalesReportPojo.setInvoicedOrdersCount(ordersCount);
        dailySalesReportPojo.setTotalRevenue(totalRevenue);
        dailySalesReportPojo.setTime(ZonedDateTime.now());
        return dailySalesReportPojo;
    }

    public List<DailySalesReportPojo> getAllDailySalesReport() {
        return dailySalesReportDao.getAllDailySalesReport();
    }
}
