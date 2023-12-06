package com.ex.erp.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTool {
    private static final LogFactory LOG = new LogFactory(JsonTool.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    public static String toJson(Object obj) {
        String json = null;
        try{
            json = mapper.writeValueAsString(obj);
        }catch (JsonProcessingException e){
            LOG.error("轉換Json發生錯誤: {0}", e);
        }
        return json;
    }
}
