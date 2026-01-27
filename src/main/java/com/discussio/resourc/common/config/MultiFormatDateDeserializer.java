package com.discussio.resourc.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 支持多种日期格式的反序列化器
 * 支持格式：
 * - yyyy-MM-dd
 * - yyyy-MM-dd HH:mm:ss
 * - yyyy-MM-dd'T'HH:mm:ss.SSS'Z' (ISO 8601 UTC)
 * - yyyy-MM-dd'T'HH:mm:ss'Z' (ISO 8601 UTC without milliseconds)
 * - yyyy-MM-dd'T'HH:mm:ss (ISO 8601 without timezone)
 */
public class MultiFormatDateDeserializer extends JsonDeserializer<Date> {
    
    private static final List<SimpleDateFormat> DATE_FORMATS = new ArrayList<>();
    private static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");
    private static final TimeZone GMT8_TIMEZONE = TimeZone.getTimeZone("GMT+8");
    
    static {
        // 添加支持的日期格式
        SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd");
        dateOnly.setTimeZone(GMT8_TIMEZONE);
        DATE_FORMATS.add(dateOnly);
        
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateTime.setTimeZone(GMT8_TIMEZONE);
        DATE_FORMATS.add(dateTime);
        
        // ISO 8601 格式（UTC时区）
        SimpleDateFormat iso8601WithMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        iso8601WithMs.setTimeZone(UTC_TIMEZONE);
        DATE_FORMATS.add(iso8601WithMs);
        
        SimpleDateFormat iso8601WithoutMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        iso8601WithoutMs.setTimeZone(UTC_TIMEZONE);
        DATE_FORMATS.add(iso8601WithoutMs);
        
        // ISO 8601 格式（无时区，使用GMT+8）
        SimpleDateFormat iso8601NoTz = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        iso8601NoTz.setTimeZone(GMT8_TIMEZONE);
        DATE_FORMATS.add(iso8601NoTz);
        
        SimpleDateFormat iso8601NoTzWithMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        iso8601NoTzWithMs.setTimeZone(GMT8_TIMEZONE);
        DATE_FORMATS.add(iso8601NoTzWithMs);
        
        // 设置所有格式为严格模式（不自动调整）
        for (SimpleDateFormat format : DATE_FORMATS) {
            format.setLenient(false);
        }
    }
    
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dateStr = jsonParser.getText();
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        dateStr = dateStr.trim();
        
        // 尝试每种格式
        for (SimpleDateFormat format : DATE_FORMATS) {
            try {
                synchronized (format) {
                    return format.parse(dateStr);
                }
            } catch (ParseException e) {
                // 继续尝试下一个格式
            }
        }
        
        // 如果所有格式都失败，抛出异常
        throw new IOException("无法解析日期格式: " + dateStr + "，支持的格式: yyyy-MM-dd, yyyy-MM-dd HH:mm:ss, ISO 8601");
    }
}
