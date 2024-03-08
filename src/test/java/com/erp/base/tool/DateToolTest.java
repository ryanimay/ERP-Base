package com.erp.base.tool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class DateToolTest {

    @Test
    void format() {
        LocalDateTime now = LocalDateTime.now();
        Assertions.assertNotEquals(now.getNano(), 0);
        String formatString = DateTool.format(now);
        Assertions.assertEquals(DateTool.parse(formatString).getNano(), 0);
    }

    @Test
    void now() {
        LocalDateTime ldtNow = LocalDateTime.now();
        LocalDateTime now = DateTool.now();
        Assertions.assertNotEquals(ldtNow, now);
        Assertions.assertEquals(ldtNow.truncatedTo(ChronoUnit.SECONDS), now);
    }
}