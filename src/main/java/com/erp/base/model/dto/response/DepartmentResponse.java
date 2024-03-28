package com.erp.base.model.dto.response;

import com.erp.base.model.dto.response.role.RoleNameResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class DepartmentResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -4L;

    private long id;
    private String name;
    private RoleNameResponse role;
}
