package com.erp.base.tool;

import com.erp.base.controller.Router;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

class ObjectToolTest {
    private static final String testString = "{\"id\":\"1\"}";
    private static final Map<String, String> map = new HashMap<>();
    static {
        map.put("id", "1");
    }

    @Test
    @DisplayName("物件轉json")
    void toJson() {
        String s = ObjectTool.toJson(map);
        Assertions.assertEquals(testString, s);
    }

    @Test
    @DisplayName("json轉物件")
    @SuppressWarnings("unchecked")
    void fromJson() throws JsonProcessingException {
        Map<String, String> s = (Map<String, String>)ObjectTool.fromJson(testString, HashMap.class);
        Assertions.assertEquals(map.keySet(), s.keySet());
        Assertions.assertEquals(map.get("id"), s.get("id"));
    }

    @Test
    @DisplayName("物件轉類")
    @SuppressWarnings("unchecked")
    void convert() {
        Map<String, String> convert = (Map<String, String>)ObjectTool.convert(map, HashMap.class);
        Assertions.assertEquals(map.keySet(), convert.keySet());
        Assertions.assertEquals(map.get("id"), convert.get("id"));
    }

    @Test
    @DisplayName("BigDecimal轉換_傳入null_返回null")
    void formatBigDecimal_null() {
        Assertions.assertNull(ObjectTool.formatBigDecimal(null));
    }

    @Test
    @DisplayName("BigDecimal轉換_傳入整數_返回整數")
    void formatBigDecimal_int() {
        String val = ObjectTool.formatBigDecimal(BigDecimal.valueOf(100.00));
        Assertions.assertEquals("100", val);
    }

    @Test
    @DisplayName("BigDecimal轉換_傳入小數點後兩位_返回小數點後兩位")
    void formatBigDecimal_float2() {
        String val = ObjectTool.formatBigDecimal(BigDecimal.valueOf(100.01));
        Assertions.assertEquals("100.01", val);
    }

    @Test
    @DisplayName("BigDecimal轉換_傳入四捨[五入]_返回到小數點後兩位")
    void formatBigDecimal_float3_up() {
        String val = ObjectTool.formatBigDecimal(BigDecimal.valueOf(100.015));
        Assertions.assertEquals("100.02", val);
    }

    @Test
    @DisplayName("BigDecimal轉換_傳入[四捨]五入_返回到小數點後兩位")
    void formatBigDecimal_float3_down() {
        String val = ObjectTool.formatBigDecimal(BigDecimal.valueOf(100.014));
        Assertions.assertEquals("100.01", val);
    }

    @Test
    @DisplayName("整理url成router格式_傳入null")
    void extractPath_null() throws URISyntaxException {
        Assertions.assertNull(ObjectTool.extractPath(null));
    }

    @Test
    @DisplayName("整理url成router格式_傳入完整requestUrl")
    void extractPath_fullUrl() throws URISyntaxException {
        String url = "https://localhost:8080/erp_base" + Router.CLIENT.OP_VALID;
        String u = ObjectTool.extractPath(url);
        Assertions.assertEquals(Router.CLIENT.OP_VALID, u);
    }

    @Test
    @DisplayName("整理url成router格式_傳入servletUrl")
    void extractPath_servletUrl() throws URISyntaxException {
        String url = "/erp_base" + Router.CLIENT.OP_VALID;
        String u = ObjectTool.extractPath(url);
        Assertions.assertEquals(Router.CLIENT.OP_VALID, u);
    }

    @Test
    @DisplayName("拿配置屬性")
    void getProperty() {
        String contextPath = ObjectTool.getProperty("server.servlet.context-path");
        Assertions.assertEquals("/erp_base", contextPath);
        String testNull = ObjectTool.getProperty("testNull");
        Assertions.assertNull(testNull);
    }
}