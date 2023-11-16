package com.ex.erp.config.security;

import com.ex.erp.service.cache.PermissionCache;
import com.ex.erp.service.cache.RoleCache;
import com.ex.erp.model.ClientModel;
import com.ex.erp.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private ClientRepository clientRepository;
    private RoleCache roleCache;
    private PermissionCache permissionCache;

    @Autowired
    public void setRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Autowired
    public void setCache(RoleCache roleCache, PermissionCache permissionCache) {
        this.roleCache = roleCache;
        this.permissionCache = permissionCache;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ClientModel client = clientRepository.findByUsername(username);
        if (client == null) {
            throw new UsernameNotFoundException("Cant find user:" + username);
        }
        return new User(client.getUsername(), client.getPassword(), getPermissionList(client.getRoleId()));
    }

    private List<GrantedAuthority> getPermissionList(int roleId) {
        Map<String, Object> roleMap = roleCache.loadData();
        Map<String, Object> permissionMap = permissionCache.loadData();
        String permissions = roleMap.get(String.valueOf(roleId)).toString();
        return Arrays.stream(permissions.split(",")).filter(permissionMap::containsKey).map(key -> (GrantedAuthority) permissionMap.get(key)).toList();
    }
}
