package com.erp.base.model.dto.request.role;

import com.erp.base.model.entity.PermissionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Schema(description = "編輯角色權限請求")
public class RolePermissionRequest {
    @Schema(description = "角色ID")
    private Long id;
    @Schema(description = "權限ID列表")
    private List<Long> permissionIds;

    public Set<PermissionModel> getPermissionSet() {
        Set<PermissionModel> set = new HashSet<>();
        permissionIds.forEach(id -> set.add(new PermissionModel(id)));
        return set;
    }
}
