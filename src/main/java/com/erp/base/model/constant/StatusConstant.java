package com.erp.base.model.constant;

import java.util.HashMap;
import java.util.Map;

public class StatusConstant {
    private static final Map<Integer, String> statusMap = new HashMap<>();
    public static final int PENDING_NO = 1;
    public static final int APPROVED_NO = 2;
    public static final int CLOSED_NO = 3;
    public static final int REMOVED_NO = 4;

    static {
        statusMap.put(PENDING_NO, "Pending");
        statusMap.put(APPROVED_NO, "Approved");
        statusMap.put(CLOSED_NO, "Closed");
        statusMap.put(REMOVED_NO, "Removed");
    }
    public static String get(int i){
        return statusMap.get(i);
    }
}
