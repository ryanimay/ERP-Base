package com.ex.erp.dto.security;

import com.ex.erp.service.security.UserDetailImpl;
import com.ex.erp.model.ClientModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 登入用戶身分
 * 從SecurityContext拿出登入用戶的資料
 * */
@Component
public class ClientIdentity {
    //當AccessToken為空時，會自動產生的字串
    private static final String EMPTY_ACCESS_TOKEN =  "anonymousUser";
    private final UserDetailImpl EMPTY_USER_DETAILS;
    private final UserDetailImpl userDetailEntity;
    @Autowired
    public ClientIdentity(UserDetailImpl userDetailImpl){
        this.EMPTY_USER_DETAILS = userDetailImpl.build(new ClientModel());
        this.userDetailEntity = getUserDetails();
    }

    public ClientModel getModel(){
        return userDetailEntity.getClientModel();
    }
    public Long getClientId(){
        return userDetailEntity.getUserId();
    }
    public String getClientName(){
        return userDetailEntity.getUsername();
    }
    public Collection<? extends GrantedAuthority> getAuthority(){
        return userDetailEntity.getAuthorities();
    }
    private UserDetailImpl getUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object userDetail = authentication.getPrincipal();
        return EMPTY_ACCESS_TOKEN.equals(userDetail) ? EMPTY_USER_DETAILS : (UserDetailImpl) userDetail;
    }
}
