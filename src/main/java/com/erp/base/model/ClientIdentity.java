package com.erp.base.model;

import com.erp.base.filter.jwt.JwtAuthenticationFilter;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.security.UserDetailImpl;
import com.erp.base.tool.ObjectTool;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;


/**
 * 登入用戶身分
 * 從SecurityContext拿出登入用戶的資料
 * */
@Component
public class ClientIdentity {

    @Value("${system.default.locale}")
    private String defaultLocaleString;
    public static Locale defaultLocale;
    @PostConstruct
    private void init() {
        defaultLocale = new Locale(defaultLocaleString);
    }
    private static final String EMPTY_USER = "anonymousUser";//SpringSecurity預設未登入使用者字段

    public static ClientModel getUser(){
        return ObjectTool.convert(getPrincipal(JwtAuthenticationFilter.PRINCIPAL_CLIENT), ClientModel.class);
    }

    public static Locale getLocale(){
        Locale locale = (Locale) getPrincipal(JwtAuthenticationFilter.PRINCIPAL_LOCALE);
        return locale == null ? defaultLocale : locale;
    }

    private static Object getPrincipal(String key){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if(EMPTY_USER.equals(principal)) return null;
        UserDetailImpl userDetails = ObjectTool.convert(principal, UserDetailImpl.class);
        Map<String, Object> dataMap = userDetails.getDataMap();
        return dataMap == null ? null : dataMap.get(key);
    }
}
