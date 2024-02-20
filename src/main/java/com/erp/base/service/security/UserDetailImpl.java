package com.erp.base.service.security;

import com.erp.base.filter.jwt.JwtAuthenticationFilter;
import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.service.CacheService;
import com.erp.base.tool.ObjectTool;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailImpl implements UserDetails {
    private Map<String, Object> dataMap;
    @JsonIgnore
    private CacheService cacheService;

    public UserDetailImpl(ClientModel clientModel, CacheService cacheService) {
        this.cacheService = cacheService;
        this.dataMap = new HashMap<>();
        this.dataMap.put(JwtAuthenticationFilter.PRINCIPAL_CLIENT, clientModel);
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return ObjectTool.convert(dataMap.get(JwtAuthenticationFilter.PRINCIPAL_CLIENT), ClientModel.class).getUsername();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return ObjectTool.convert(dataMap.get(JwtAuthenticationFilter.PRINCIPAL_CLIENT), ClientModel.class).getPassword();
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return ObjectTool.convert(dataMap.get(JwtAuthenticationFilter.PRINCIPAL_CLIENT), ClientModel.class).isActive();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !ObjectTool.convert(dataMap.get(JwtAuthenticationFilter.PRINCIPAL_CLIENT), ClientModel.class).isLock();
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRolePermission(ObjectTool.convert(dataMap.get(JwtAuthenticationFilter.PRINCIPAL_CLIENT), ClientModel.class).getRoles());
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    private Collection<? extends GrantedAuthority> getRolePermission(Set<RoleModel> roles) {
        Set<RolePermissionDto> set = new HashSet<>();
        for (RoleModel role : roles) {
            set.addAll(cacheService.getRolePermission(role.getId()));
        }
        return set;
    }
}
