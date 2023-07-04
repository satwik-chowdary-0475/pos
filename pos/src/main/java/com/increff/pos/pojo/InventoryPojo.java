package com.increff.pos.pojo;

import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(
        name="inventory"
)
public class InventoryPojo extends AbstractPojo {
    @Id
    private Integer id;
    @Column(nullable = false)
    private Integer quantity;
}
