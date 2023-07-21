package com.increff.pos.service;

import com.increff.pos.dao.DailySalesReportDao;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.DailySalesReportPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailySalesReportService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private DailySalesReportDao dailySalesReportDao;

    @Transactional
    public void insert() {
        List<OrderPojo> orderPojoList = getInvoicedOrdersBeforeDate();

        Object[] dailySalesReport = getOrderItemsReport(orderPojoList);
        Integer totalInvoicedItems = ((Number) dailySalesReport[0]).intValue();
        Double totalRevenue = ((Double) dailySalesReport[1]);

        DailySalesReportPojo dailySalesReportPojo = setDailySalesReport(totalInvoicedItems, totalRevenue, orderPojoList.size());
        dailySalesReportDao.insert(dailySalesReportPojo);
    }

    private List<OrderPojo> getInvoicedOrdersBeforeDate() {
        LocalDate currentDate = LocalDate.now();
        Date currentDay = Date.valueOf(currentDate);
        LocalDate previousDate = currentDate.minusDays(1);
        Date previousDay = Date.valueOf(previousDate);
        return orderDao.getByDate(previousDay, currentDay, OrderStatus.INVOICED);
    }

    private Object[] getOrderItemsReport(List<OrderPojo> orderPojoList) {
        if (!orderPojoList.isEmpty()) {
            List<Integer> orderIdsList = orderPojoList.stream().map(OrderPojo::getId).collect(Collectors.toList());
            return orderItemDao.getOrderItemsReport(orderIdsList);
        }
        return new Object[]{0, 0.0};
    }

    public DailySalesReportPojo setDailySalesReport(Integer totalInvoicedItems, Double totalRevenue, int ordersCount) {
        DailySalesReportPojo dailySalesReportPojo = new DailySalesReportPojo();
        dailySalesReportPojo.setInvoicedItemsCount(totalInvoicedItems);
        dailySalesReportPojo.setInvoicedOrdersCount(ordersCount);
        dailySalesReportPojo.setTotalRevenue(totalRevenue);
        dailySalesReportPojo.setTime(ZonedDateTime.now());
        return dailySalesReportPojo;
    }

    public List<DailySalesReportPojo> getAll() {
        return dailySalesReportDao.getAll();
    }
}
