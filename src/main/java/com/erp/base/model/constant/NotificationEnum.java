package com.erp.base.model.constant;

/**
 * 固定創建通知的格式(顯示I18nKey、轉跳路徑、是否全域通知)
 * info由前端轉換語系，因為要在DB做持久化，會導致沒辦法動態轉換
 * */
public enum NotificationEnum {
    UPDATE_USER("notification.updateUser", "client", false),
    EDIT_SALARY_ROOT("notification.editSalaryRoot", "salaryList", false),
    ADD_PERFORMANCE("notification.addPerformance", "performanceList", false),
    ACCEPT_PERFORMANCE("notification.acceptPerformance", "personalPerformance", false),
    ADD_LEAVE("notification.addLeave", "AddLeave", false),
    ACCEPT_LEAVE("notification.acceptLeave", "AcceptLeave", false)

    ;
    private final String info;
    private final String routerName;
    private final boolean global;

    NotificationEnum(String info, String routerName, boolean global) {
        this.info = info;
        this.routerName = routerName;
        this.global = global;
    }

    public String getInfo(Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append(info);
        sb.append("++");
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i]);
            if (i < params.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public String getRouterName() {
        return routerName;
    }

    public boolean getGlobal() {
        return global;
    }
}
