package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

// ENUM STRING
@Setter
@Getter
@Entity
@Table(name = "orders")
public class OrderPojo extends AbstractPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "order_code", nullable = false)
    private String orderCode;
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    @Column(nullable = false)
    private String status;
    private ZonedDateTime time;
}
