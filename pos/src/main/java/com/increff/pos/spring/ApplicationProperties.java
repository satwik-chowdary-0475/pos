package com.increff.pos.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ApplicationProperties {
    @Value("${jdbc.driverClassName}")
    public String jdbcDriver;
    @Value("${jdbc.url}")
    public String jdbcUrl;
    @Value("${jdbc.username}")
    public String jdbcUsername;
    @Value("${jdbc.password}")
    public String jdbcPassword;
    @Value("${hibernate.dialect}")
    public String hibernateDialect;
    @Value("${hibernate.show_sql}")
    public String hibernateShowSql;
    @Value("${hibernate.hbm2ddl.auto}")
    public String hibernateHbm2ddl;
    @Value("${hibernate.physical_naming_strategy}")
    public String hibernatePhysicalNamingStrategy;

    public static List<String> adminsList;

    @Value("${admins}")
    private void setAdmins(String admins) {
        adminsList = Arrays.asList(admins.split(","));
    }

}
