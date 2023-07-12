package com.increff.pos.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class RoundUtil {
    public static Double round(Double dbl) {
        if (Objects.isNull(dbl)) {
            return null;
        }
        BigDecimal roundedValue = BigDecimal.valueOf(dbl).setScale(2, RoundingMode.DOWN);
        return roundedValue.doubleValue();
    }

}