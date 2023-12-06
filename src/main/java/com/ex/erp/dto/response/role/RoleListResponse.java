package com.ex.erp.dto.response.role;

import com.ex.erp.model.RoleModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleListResponse {
    private long id;
    private String roleName;

    public RoleListResponse(RoleModel model) {
        this.id = model.getId();
        this.roleName = model.getRoleName();
    }
}
