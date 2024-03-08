package com.erp.base.model.constant;

import com.erp.base.model.ClientIdentity;
import com.erp.base.tool.BeanProviderTool;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * 固定創建通知的格式(顯示I18n、轉跳路徑、是否全域通知)
 * */
public enum NotificationEnum {
    UPDATE_USER("notification.updateUser", "UserInfo", false),
    EDIT_SALARY_ROOT("notification.editSalaryRoot", "EditSalaryRoot", false),
    ADD_PERFORMANCE("notification.addPerformance", "AddPerformance", false),
    ACCEPT_PERFORMANCE("notification.acceptPerformance", "AcceptPerformance", false),
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
        MessageSource messageSource = BeanProviderTool.getBean(MessageSource.class);
        Locale locale = ClientIdentity.getLocale();
        return messageSource.getMessage(info, params, locale);
    }

    public String getRouterName() {
        return routerName;
    }

    public boolean getGlobal() {
        return global;
    }
}
