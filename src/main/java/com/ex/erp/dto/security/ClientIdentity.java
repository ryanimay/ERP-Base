package com.ex.erp.dto.security;

import com.ex.erp.dto.response.ClientResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 登入用戶身分
 * 從SecurityContext拿出登入用戶的資料
 * */
public class ClientIdentity {
    private static final String EMPTY_USER = "anonymousUser";//SpringSecurity預設未登入使用者字段
    public static ClientResponse getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        return EMPTY_USER.equals(principal) ? null : (ClientResponse)principal;
    }
}
