package com.erp.base.model.dto.request.role;

import com.erp.base.model.entity.RouterModel;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class RoleRouterRequest {
    private Long id;
    private List<Long> routerIds;

    public Set<RouterModel> getRouterSet() {
        Set<RouterModel> set = new HashSet<>();
        routerIds.forEach(id -> set.add(new RouterModel(id)));
        return set;
    }
}
