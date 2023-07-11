package com.increff.pos.service;

import com.increff.pos.model.data.ErrorData;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class ApiException extends Exception {
    private static final long serialVersionUID = 1L;
    private static List<ErrorData> errorDataList;

    public ApiException(String string) {
        super(string);
    }

    public ApiException(List<ErrorData> errorDataList) {
        this.errorDataList = errorDataList;
    }
    public List<ErrorData> getErrorDataList() {
        return errorDataList;
    }

}
