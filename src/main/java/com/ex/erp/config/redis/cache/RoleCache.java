package com.ex.erp.config.redis.cache;

import com.ex.erp.model.RoleModel;
import com.ex.erp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleCache extends ICache{
    private static final String REDIS_KEY = "role_data";
    private final RoleRepository roleRepository;
    @Autowired
    public RoleCache(RedisTemplate<String, Object> redis, RoleRepository roleRepository1) {
        super(redis, REDIS_KEY);
        this.roleRepository = roleRepository1;
    }

    @Override
    protected Map<String, Object> findData() {
        Map<String, Object> roleMap = new HashMap<>();
        List<RoleModel> roleList = roleRepository.findAll();
        for(RoleModel model : roleList){
            String roleId = String.valueOf(model.getId());
            String permissionIds = roleMap.getOrDefault(roleId, "") + "," + model.getPermissionId();
            roleMap.put(roleId, permissionIds);
        }
        redis.opsForValue().set(REDIS_KEY, roleMap);
        return roleMap;
    }
}
