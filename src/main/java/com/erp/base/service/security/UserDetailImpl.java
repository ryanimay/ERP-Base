package com.erp.base.service.security;

import com.erp.base.model.ClientModel;
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
    private ClientModel ClientModel;
    private ClientCache ClientCache;

    public UserDetailImpl(ClientModel clientModel, ClientCache clientCache) {
        ClientModel = clientModel;
        ClientCache = clientCache;
    }

    @Override
    public String getUsername() {
        return ClientModel.getUsername();
    }

    @Override
    public String getPassword() {
        return ClientModel.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return ClientModel.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !ClientModel.isLock();
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
