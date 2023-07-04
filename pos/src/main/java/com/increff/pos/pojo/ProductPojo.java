package com.increff.pos.pojo;

import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(
        name = "product",
        uniqueConstraints =
        @UniqueConstraint(columnNames={"barcode"})
)
public class ProductPojo extends AbstractPojo{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    @Column(nullable = false)
    private String barcode;
    @Column(name = "brand_category_id" , nullable = false)
    private Integer brandCategory;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double mrp;
}
