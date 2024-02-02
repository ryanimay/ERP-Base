package com.erp.base.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 採購常數
 * */
public class ProcurementConstant {
    //待進行
    public static final int STATUS_PENDING = 1;
    //進行中
    public static final int STATUS_APPROVED = 2;
    //完成
    public static final int STATUS_DONE = 3;
    private static final Map<Integer, String> statusMap= new HashMap<>();
    static {
        statusMap.put(STATUS_PENDING, "pending");
        statusMap.put(STATUS_APPROVED, "approved");
        statusMap.put(STATUS_DONE, "done");
    }

    public static String get(int i){
        return statusMap.get(i);
    }
}
