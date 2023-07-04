package com.increff.pos.model.data;

import com.increff.pos.model.form.InventoryForm;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InventoryData extends InventoryForm {
    private Integer id;
    private String productName;
}
