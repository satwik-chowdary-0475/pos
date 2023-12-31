package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(
        name = "order_items",
        indexes = @Index(columnList = "orderId")
)
public class OrderItemPojo extends AbstractPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(nullable = false)
    private Integer orderId;
    @Column(nullable = false)
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private Double sellingPrice;
}
