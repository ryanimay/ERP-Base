package com.erp.base.model;

import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.service.security.UserDetailImpl;
import com.erp.base.tool.ObjectTool;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;


/**
 * 登入用戶身分
 * 從SecurityContext拿出登入用戶的資料
 * */
public class ClientIdentity {
    public static Locale defaultLocale = new Locale("zh", "TW");//預設中文
    private static final String EMPTY_USER = "anonymousUser";//SpringSecurity預設未登入使用者字段

    //用dto來做常態性的用戶身分，不然懶加載"極度"難搞
    public static ClientIdentityDto getUser(){
        UserDetailImpl principal = getPrincipal();
        return principal == null ? null : principal.getClientModel();
    }

    public static Locale getLocale(){
        UserDetailImpl principal = getPrincipal();
        return principal == null ? defaultLocale : principal.getLocale();
    }

    private static UserDetailImpl getPrincipal(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if(EMPTY_USER.equals(principal)) return null;
        return ObjectTool.convert(principal, UserDetailImpl.class);
    }
}
