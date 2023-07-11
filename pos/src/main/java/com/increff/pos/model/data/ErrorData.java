package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorData {
    private Integer row;
    private String errorMessage;
}
