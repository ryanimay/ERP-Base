package com.erp.base.tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTool {
    public static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS);
    private DateTool(){}

    public static LocalDateTime parse(String stringTime){
        return LocalDateTime.parse(stringTime, formatter);
    }

    /**
     * LocalDateTime轉成固定格式String
     * */
    public static String format(LocalDateTime time){
        return time == null ? null : time.format(formatter);
    }

    /**
     * 現在時間, 截斷秒
     * */
    public static LocalDateTime now(){
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
