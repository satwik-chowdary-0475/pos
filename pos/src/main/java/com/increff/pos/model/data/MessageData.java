package com.increff.pos.model.data;


import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class MessageData {
    private String message;
    private List<ErrorData> errorDataList;
}