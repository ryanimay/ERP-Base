package com.ex.erp.service.security;

import com.ex.erp.model.ClientModel;
import com.ex.erp.service.cache.PermissionCache;
import com.ex.erp.service.cache.RoleCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Service
public class UserDetailImpl implements UserDetails {
    private ClientModel ClientModel;
    private RoleCache roleCache;
    private PermissionCache permissionCache;

    public UserDetailImpl build(ClientModel client) {
        ClientModel = client;
        return this;
    }

    @Autowired
    public void setCache(RoleCache roleCache, PermissionCache permissionCache) {
        this.roleCache = roleCache;
        this.permissionCache = permissionCache;
    }

    public ClientModel getClientModel() {
        return ClientModel;
    }

    public Long getUserId(){
        return ClientModel.getId();
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
        return getPermissionList(ClientModel.getRoleId());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    private Collection<? extends GrantedAuthority> getPermissionList(int roleId) {
        Map<String, Object> roleMap = roleCache.loadData();
        Map<String, Object> permissionMap = permissionCache.loadData();
        String permissions = roleMap.get(String.valueOf(roleId)).toString();
        return Arrays.stream(permissions.split(","))
                .filter(permissionMap::containsKey)
                .map(key -> (GrantedAuthority) permissionMap.get(key))
                .toList();
    }
}
