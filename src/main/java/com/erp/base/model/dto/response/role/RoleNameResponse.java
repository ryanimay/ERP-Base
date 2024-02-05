package com.erp.base.model.dto.response.role;

import com.erp.base.model.entity.RoleModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleNameResponse {
    private long id;
    private String roleName;

    public RoleNameResponse(RoleModel model) {
        this.id = model.getId();
        this.roleName = model.getRoleName();
    }
}
