package com.increff.pos.util;

import java.util.Objects;

public class StringUtil {
    public static String toLowerCase(String s) {
        return Objects.isNull(s) ? null : s.trim().toLowerCase();
    }

}