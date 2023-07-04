package com.increff.pos.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class StringUtil {

	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static String toLowerCase(String s) {
		return s == null ? null : s.trim().toLowerCase();
	}

	public static ZonedDateTime convertZonedDateTime(String s){
		DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
		return s == null ? null : ZonedDateTime.parse(s, formatter);
	}
}