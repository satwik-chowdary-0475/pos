package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedData {
    private Integer totalCount;
    private List<?> dataList;
    public PaginatedData(List<?> dataList,Integer totalCount) {
        this.totalCount = totalCount;
        this.dataList = dataList;
    }
}
