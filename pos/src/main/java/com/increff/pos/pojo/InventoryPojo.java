package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(
        name = "inventory"
)
public class InventoryPojo extends AbstractPojo {
    @Id
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
}
