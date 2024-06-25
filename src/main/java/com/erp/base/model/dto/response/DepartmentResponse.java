package com.erp.base.model.dto.response;

import com.erp.base.model.dto.response.role.RoleNameResponse;
import com.erp.base.model.entity.DepartmentModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class DepartmentResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -4L;

    private long id;
    private String name;
    private RoleNameResponse role;
    private Set<RoleNameResponse> roles = new HashSet<>();

    @SuppressWarnings("unused")
    public DepartmentResponse(DepartmentModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.role = new RoleNameResponse(model.getDefaultRole());
        model.getRoles().forEach(roleModel -> this.roles.add(new RoleNameResponse(roleModel)));
    }
}
