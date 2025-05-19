package com.zcunsoft.clklog.api.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 时间格式化工具
 */
public class Formatters {

    private static final DateTimeFormatter instantDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter instantDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    /**
     * 转化为 yyyy-MM-dd 格式
     *
     * @param date 时间戳
     * @return yyyy-MM-dd格式的日期
     */
    public static String toDateString(Timestamp date) {
        return instantDateFormatter.format(date.toInstant());
    }

    /**
     * 转化为 yyyy-MM-dd 格式
     *
     * @param date 时间戳
     * @return yyyy-MM-dd格式的日期
     */
    public static String toDateString(LocalDate date) {
        return instantDateFormatter.format(date);
    }

    /**
     * 转化为 yyyy-MM-dd 格式
     *
     * @param date 时间
     * @return yyyy-MM-dd格式的日期
     */
    public static String toDateString(Instant date) {
        return instantDateFormatter.format(date);
    }

    /**
     * 转化为 yyyy-MM-dd HH:mm:ss 格式
     *
     * @param date 时间戳
     * @return yyyy-MM-dd HH:mm:ss格式的时间
     */
    public static String toDateTimeString(Timestamp date) {
        return instantDateTimeFormatter.format(date.toInstant());
    }

    /**
     * 转化为 yyyy-MM-dd HH:mm:ss 格式
     *
     * @param date 时间
     * @return yyyy-MM-dd HH:mm:ss格式的时间
     */
    public static String toDateTimeString(LocalDate date) {
        return instantDateTimeFormatter.format(date);
    }

    /**
     * 转化为 yyyy-MM-dd HH:mm:ss 格式
     *
     * @param date 时间
     * @return yyyy-MM-dd HH:mm:ss格式的时间
     */
    public static String toDateTimeString(Instant date) {
        return instantDateTimeFormatter.format(date);
    }

}
