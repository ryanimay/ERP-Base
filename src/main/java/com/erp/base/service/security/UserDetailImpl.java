package com.erp.base.service.security;

import com.erp.base.model.UserModel;
import com.erp.base.service.cache.ClientCache;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
@Getter
@Setter
@Transactional
public class UserDetailImpl implements UserDetails {
    private UserModel UserModel;
    private ClientCache ClientCache;

    public UserDetailImpl(UserModel userModel, ClientCache clientCache) {
        UserModel = userModel;
        ClientCache = clientCache;
    }

    @Override
    public String getUsername() {
        return UserModel.getUsername();
    }

    @Override
    public String getPassword() {
        return UserModel.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return UserModel.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !UserModel.isLock();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}
