package com.erp.base.model.dto.response;

import com.erp.base.model.entity.RouterModel;
import lombok.Data;

@Data
public class RouterResponse {
    private long id;
    private String name;

    public RouterResponse(RouterModel model) {
        this.id = model.getId();
        this.name = model.getName();
    }
}
