package com.erp.base.service.security;

import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.service.CacheService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailImpl implements UserDetails {
    private Locale locale;
    private ClientIdentityDto clientModel;
    @JsonIgnore
    private CacheService cacheService;

    public UserDetailImpl(String lang, ClientIdentityDto clientModel, CacheService cacheService) {
        this.locale = new Locale(lang);
        this.cacheService = cacheService;
        this.clientModel = clientModel;
    }

    public UserDetailImpl(ClientIdentityDto clientModel, CacheService cacheService) {
        this.cacheService = cacheService;
        this.clientModel = clientModel;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return clientModel.getUsername();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return clientModel.getPassword();
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return clientModel.isActive();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !clientModel.isLock();
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRolePermission();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    private Collection<? extends GrantedAuthority> getRolePermission() {
        ClientIdentityDto client = cacheService.getClient(clientModel.getId());
        Set<RolePermissionDto> set = new HashSet<>();
        for (Long role : client.getRoleId()) {
            set.addAll(cacheService.getRolePermission(role));
        }
        return set;
    }
}
