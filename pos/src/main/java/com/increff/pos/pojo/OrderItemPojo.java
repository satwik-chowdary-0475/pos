package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(
        name = "order_items"
)
public class OrderItemPojo extends AbstractPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "order_id", nullable = false)
    private Integer orderId;
    @Column(name = "product_id", nullable = false)
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
    @Column(name = "selling_price", nullable = false)
    private Double sellingPrice;
}
