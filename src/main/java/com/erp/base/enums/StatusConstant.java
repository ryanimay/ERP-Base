package com.erp.base.enums;

import java.util.HashMap;
import java.util.Map;

public class StatusConstant {
    private static final Map<Integer, String> statusMap = new HashMap<>();

    static {
        statusMap.put(1, "Pending");
        statusMap.put(2, "Approved");
        statusMap.put(3, "Closed");
        statusMap.put(4, "Removed");
    }
    public static String get(int i){
        return statusMap.get(i);
    }
}
