package com.erp.base.model.constant;

import com.erp.base.model.dto.response.LeaveTypeResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveConstant {
    private static final Map<Integer, String> leaveMap = new HashMap<>();

    static {
        leaveMap.put(1, "leaveType.annual");
        leaveMap.put(2, "leaveType.sick");
        leaveMap.put(3, "leaveType.maternity");
        leaveMap.put(4, "leaveType.paternity");
        leaveMap.put(5, "leaveType.marriage");
        leaveMap.put(6, "leaveType.bereavement");
        leaveMap.put(7, "leaveType.publicHoliday");

    }

    public static String get(int id) {
        return leaveMap.get(id);
    }

    public static List<LeaveTypeResponse> list() {
        return leaveMap.keySet().stream().map(key -> new LeaveTypeResponse(key, get(key))).toList();
    }
}
