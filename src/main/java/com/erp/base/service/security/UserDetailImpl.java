package com.erp.base.service.security;

import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.service.cache.ClientCache;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Transactional
public class UserDetailImpl implements UserDetails {
    private ClientModel clientModel;
    private ClientCache clientCache;

    public UserDetailImpl(ClientModel clientModel, ClientCache clientCache) {
        this.clientModel = clientModel;
        this.clientCache = clientCache;
    }

    @Override
    public String getUsername() {
        return clientModel.getUsername();
    }

    @Override
    public String getPassword() {
        return clientModel.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return clientModel.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !clientModel.isLock();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRolePermission(clientModel.getRoles());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    private Collection<? extends GrantedAuthority> getRolePermission(Set<RoleModel> roles) {
        Set<RolePermissionDto> set = new HashSet<>();
        for(RoleModel role : roles){
            set.addAll(clientCache.getRolePermission(role.getId()));
        }
        return set;
    }
}
