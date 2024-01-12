package com.erp.base.model.dto.response;

import com.erp.base.model.entity.RouterModel;
import lombok.Data;

@Data
public class RouterConfigResponse {
    private String path;
    private String name;
    private String components;
    private String metas;

    public RouterConfigResponse(RouterModel model) {
        this.path = model.getPath();
        this.name = model.getName();
        this.components = model.getComponents();
        this.metas = model.getMetas();
    }
}
