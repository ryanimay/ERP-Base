package com.erp.base.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class ObjectTool {
    private static final Properties properties = new Properties();
    private static final LogFactory LOG = new LogFactory(ObjectTool.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String contextPath;
    static {
        mapper.registerModule(new JavaTimeModule());

        try (InputStream input = ObjectTool.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                LOG.error("Unable to find application.properties");
            }
        } catch (IOException e) {
            LOG.error("Error loading application.properties", e);
        }

        contextPath = getProperty("server.servlet.context-path");
    }

    public static String toJson(Object obj) {
        String json = null;
        try{
            json = mapper.writeValueAsString(obj);

        }catch (JsonProcessingException e){
            LOG.error("轉換Json發生錯誤: {0}", e.getMessage());

        }
        return json;
    }

    public static <T> T fromJson(String fromValue, Class<T> toValueType) throws JsonProcessingException {
        return mapper.readValue(fromValue, toValueType);
    }

    public static <T> T convert(Object fromValue, Class<T> toValueType){
        return mapper.convertValue(fromValue, toValueType);
    }

    public static String formatBigDecimal(BigDecimal num){
        if (num == null) {
            return null;
        }
        if (num.stripTrailingZeros().scale() <= 0) {
            return num.toBigInteger().toString();
        } else {
            return num.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toString();
        }
    }

    public static String extractPath(String requestUrl) throws URISyntaxException {
        if(requestUrl == null || requestUrl.isEmpty()) return null;
        URI uri = new URI(requestUrl);
        return uri.getPath().replace(contextPath, "");
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
