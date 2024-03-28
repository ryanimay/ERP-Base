package com.erp.base.model.dto.request.role;

import com.erp.base.model.entity.MenuModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Schema(description = "編輯角色菜單權限請求")
public class RoleMenuRequest {
    @Schema(description = "角色ID")
    private Long id;
    @Schema(description = "菜單ID列表")
    private List<Long> menuIds;

    public Set<MenuModel> getMenuSet() {
        Set<MenuModel> set = new HashSet<>();
        menuIds.forEach(id -> set.add(new MenuModel(id)));
        return set;
    }
}
