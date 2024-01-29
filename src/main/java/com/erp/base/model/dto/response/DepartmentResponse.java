package com.erp.base.model.dto.response;

import com.erp.base.model.entity.DepartmentModel;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class DepartmentResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -2L;

    private long id;
    private String name;

    public DepartmentResponse(DepartmentModel model) {
        this.id = model.getId();
        this.name = model.getName();
    }
}
