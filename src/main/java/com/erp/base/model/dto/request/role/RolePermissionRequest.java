package com.erp.base.model.dto.request.role;

import com.erp.base.model.entity.PermissionModel;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class RolePermissionRequest {
    private Long id;
    private List<Long> permissionIds;

    public Set<PermissionModel> getPermissionSet() {
        Set<PermissionModel> set = new HashSet<>();
        permissionIds.forEach(id -> set.add(new PermissionModel(id)));
        return set;
    }
}
