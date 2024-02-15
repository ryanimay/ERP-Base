package com.erp.base.model.dto.request.role;

import com.erp.base.model.entity.RouterModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Schema(description = "編輯角色頁面權限請求")
public class RoleRouterRequest {
    @Schema(description = "角色ID")
    private Long id;
    @Schema(description = "頁面權限ID列表")
    private List<Long> routerIds;

    public Set<RouterModel> getRouterSet() {
        Set<RouterModel> set = new HashSet<>();
        routerIds.forEach(id -> set.add(new RouterModel(id)));
        return set;
    }
}
