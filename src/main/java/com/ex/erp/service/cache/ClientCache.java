package com.ex.erp.service.cache;

import com.ex.erp.dto.response.ClientResponse;
import com.ex.erp.model.ClientModel;
import com.ex.erp.model.PermissionModel;
import com.ex.erp.model.RoleModel;
import com.ex.erp.repository.PermissionRepository;
import com.ex.erp.repository.RoleRepository;
import com.ex.erp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "client")
public class ClientCache {
    private ClientService clientService;
    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;
    @Autowired
    public void setPermissionRepository(PermissionRepository permissionRepository){
        this.permissionRepository = permissionRepository;
    }
    @Autowired
    public void setRoleRepository(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }
    @Autowired
    public void setClientService(ClientService clientService){
        this.clientService = clientService;
    }

    //有關使用者資訊，不存密碼
    @Cacheable(key = "'clientCache_' + #username")
    public ClientResponse getClient(String username) {
        ClientModel clientModel = clientService.findByUsername(username);
        return new ClientResponse(clientModel);
    }

    @CacheEvict(key = "'clientCache_' + #username")
    public void refreshClient(String username) {
    }

    @CacheEvict(allEntries = true)
    public void refreshClientAll() {
    }

    @Cacheable(key = "'roleCache'")
    public Map<String, Object> getRole() {
        Map<String, Object> roleMap = new HashMap<>();
        List<RoleModel> roleList = roleRepository.findAll();
        for(RoleModel model : roleList){
            String roleId = String.valueOf(model.getId());
            String permissionIds = roleMap.getOrDefault(roleId, "") + "," + model.getPermissionId();
            roleMap.put(roleId, permissionIds);
        }
        return roleMap;
    }

    @Cacheable(key = "'permissionCache'")
    public Map<String, Object> getPermission() {
        List<PermissionModel> permissionList = permissionRepository.findAll();
        return permissionList.stream()
                .collect(Collectors.toMap(model -> String.valueOf(model.getId()), Function.identity()));
    }

    //角色擁有權限
    @Cacheable(key = "'rolePermission_' + #roleId")
    public Collection<? extends GrantedAuthority> getRolePermission(int roleId) {
        Map<String, Object> roleMap = getRole();
        Map<String, Object> permissionMap = getPermission();
        String permissions = roleMap.get(String.valueOf(roleId)).toString();
        return Arrays.stream(permissions.split(","))
                .filter(permissionMap::containsKey)
                .map(key -> (GrantedAuthority) permissionMap.get(key))
                .toList();
    }
}
