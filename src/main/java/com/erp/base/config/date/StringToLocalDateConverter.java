package com.erp.base.config.date;

import com.erp.base.tool.DateTool;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTool.YYYY_MM_DD);
        // yyyy-MM 轉為 yyyy-MM-01
        return LocalDate.parse(source + "-01", formatter);
    }
}
