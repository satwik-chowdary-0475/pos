package com.increff.pos.spring;

import com.increff.pos.service.DailySalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulerConfig {

    @Autowired
    private DailySalesReportService dailySalesReportService;

    @Scheduled(cron = "0 0 12 * * ?")
    public void createDailySalesReport() {
        dailySalesReportService.insertDailySalesReport();
    }

}
