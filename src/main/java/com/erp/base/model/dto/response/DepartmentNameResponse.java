package com.erp.base.model.dto.response;

import com.erp.base.model.entity.DepartmentModel;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class DepartmentNameResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -2L;

    private long id;
    private String name;

    public DepartmentNameResponse() {
    }

    public DepartmentNameResponse(DepartmentModel model) {
        this.id = model.getId();
        this.name = model.getName();
    }
}
