package com.erp.base.enums;

import com.erp.base.model.ClientIdentity;
import com.erp.base.tool.BeanProviderTool;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * 固定創建通知的格式
 * */
public enum NotificationEnum {
    UPDATE_USER("notification.updateUser", "UserInfo", false),
    EDIT_SALARY_ROOT("notification.editSalaryRoot", "EditSalaryRoot", false)
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
