package com.ex.erp.config.redis.cache;

import com.ex.erp.model.PermissionModel;
import com.ex.erp.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
public class PermissionCache extends ICache{
    private static final String REDIS_KEY = "permission_data";
    private final PermissionRepository permissionRepository;
    @Autowired
    public PermissionCache(RedisTemplate<String, Object> redis, PermissionRepository permissionRepository) {
        super(redis, REDIS_KEY);
        this.permissionRepository = permissionRepository;
    }

    @Override
    protected Map<String, Object> findData() {
        List<PermissionModel> permissionList = permissionRepository.findAll();
        Map<String, Object> roleMap = permissionList.stream()
                .collect(Collectors.toMap(model -> String.valueOf(model.getId()), Function.identity()));
        redis.opsForValue().set(REDIS_KEY, roleMap);
        return roleMap;
    }
}
