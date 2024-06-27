package com.erp.base.model.dto.response;

import com.erp.base.model.dto.response.role.RoleNameResponse;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.RoleModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
public class DepartmentResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -4L;

    private long id;
    private String name;
    private RoleNameResponse role;
    private List<RoleNameResponse> roles = new ArrayList<>();

    @SuppressWarnings("unused")
    public DepartmentResponse(DepartmentModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.role = new RoleNameResponse(model.getDefaultRole());
        model.getRoles().stream().sorted(Comparator
                .comparingInt(RoleModel::getLevel).reversed()
                .thenComparing(RoleModel::getId)).forEach(roleModel -> this.roles.add(new RoleNameResponse(roleModel)));
    }
}
