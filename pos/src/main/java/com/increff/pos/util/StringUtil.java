package com.increff.pos.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class StringUtil {

    public static boolean isEmpty(String s) {
        return Objects.isNull(s) || s.trim().length() == 0;
    }

    public static String toLowerCase(String s) {
        return Objects.isNull(s) ? null : s.trim().toLowerCase();
    }

    public static ZonedDateTime convertZonedDateTime(String s) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        return Objects.isNull(s) ? null : ZonedDateTime.parse(s, formatter);
    }
}