package com.erp.base.model.dto.response;

import com.erp.base.model.dto.response.role.RoleNameResponse;
import com.erp.base.model.entity.DepartmentModel;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DepartmentResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -4L;

    private long id;
    private String name;
    private RoleNameResponse role;

    public DepartmentResponse(DepartmentModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.role = new RoleNameResponse(model.getDefaultRole());
    }
}
