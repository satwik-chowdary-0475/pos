package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(
        name="brand",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"brand", "category"})
)
public class BrandPojo extends AbstractPojo{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private String category;


}
