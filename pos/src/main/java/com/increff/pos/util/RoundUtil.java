package com.increff.pos.util;

import java.util.Objects;

public class RoundUtil {
    public static Double round(Double dbl) {
        if (Objects.isNull(dbl)) return null;
        Double roundVal = (double) (Math.round(dbl * 100.0) / 100.0);
        return roundVal;
    }

}