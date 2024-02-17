package com.erp.base.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectTool {
    private static final LogFactory LOG = new LogFactory(ObjectTool.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.registerModule(new JavaTimeModule());
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

    public static <T> T convert(Object fromValue, Class<T> toValueType){
        return mapper.convertValue(fromValue, toValueType);
    }
}