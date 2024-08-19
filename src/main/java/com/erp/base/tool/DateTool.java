package com.erp.base.tool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTool {
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
    private DateTool(){}

    public static LocalDateTime parseDateTime(String stringTime){
        return LocalDateTime.parse(stringTime, dateTimeFormatter);
    }

    public static LocalDate parseDate(String stringTime){
        return LocalDate.parse(stringTime, dateFormatter);
    }

    /**
     * LocalDateTime轉成固定格式String
     * */
    public static String format(LocalDateTime time){
        return time == null ? null : time.format(dateTimeFormatter);
    }
    /**
     * LocalDate轉成固定格式String
     * */
    public static String formatDate(LocalDate time){
        return time == null ? null : time.format(dateFormatter);
    }

    /**
     * 現在時間, 截斷秒
     * */
    public static LocalDateTime now(){
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
