package com.discussio.resourc.common.support;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类型转换工具类
 */
public class ConvertUtil {
    
    public static Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String toString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static Date toDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof String) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sdf.parse((String) value);
            } catch (ParseException e) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return sdf.parse((String) value);
                } catch (ParseException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    public static String[] toStrArray(String str) {
        if (str == null || str.isEmpty()) {
            return new String[0];
        }
        return str.split(",");
    }
}
