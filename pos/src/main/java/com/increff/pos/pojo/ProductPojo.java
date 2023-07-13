package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(
        name = "product",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"barcode"})
)
public class ProductPojo extends AbstractPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(nullable = false)
    private String barcode;
    @Column(nullable = false)
    private Integer brandCategoryId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double mrp;
}
