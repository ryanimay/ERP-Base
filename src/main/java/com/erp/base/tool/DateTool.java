package com.erp.base.tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTool {
    public static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
    private DateTool(){}
    public static LocalDateTime convert(LocalDateTime time, DateTimeFormatter from, DateTimeFormatter to){
        return parse(format(time, from), to);
    }
    public static String format(LocalDateTime time, DateTimeFormatter formatter){
        return time.format(formatter);
    }
    public static LocalDateTime parse(String time, DateTimeFormatter formatter){
        return LocalDateTime.parse(time, formatter);
    }
    public static LocalDateTime now(){
        return LocalDateTime.now().withNano(0);
    }
}
