package com.erp.base.model.constant;

import com.erp.base.model.ClientIdentity;
import com.erp.base.tool.BeanProviderTool;
import org.springframework.context.MessageSource;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LeaveConstant {
    private static final MessageSource messageSource = BeanProviderTool.getBean(MessageSource.class);

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
        return messageSource.getMessage(leaveMap.get(id), null, ClientIdentity.getLocale());
    }

    public static List<String> list() {
        Locale locale = ClientIdentity.getLocale();
        return leaveMap.values().stream().map(v -> messageSource.getMessage(v, null, locale)).toList();
    }
}
