package com.ex.erp.service.security;

import com.ex.erp.model.ClientModel;
import com.ex.erp.service.cache.ClientCache;
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
        return ClientCache.getRolePermission(ClientModel.getRole());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}
