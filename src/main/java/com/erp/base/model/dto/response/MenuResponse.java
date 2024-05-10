package com.erp.base.model.dto.response;

import com.erp.base.model.entity.MenuModel;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class MenuResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -6L;
    private long id;
    private String name;
    private String path;
    private String icon;
    private long parentsId;
    private List<MenuResponse> child;
    private Integer level;
    private Integer order;

    public MenuResponse(MenuModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.path = model.getPath();
        this.icon = model.getIcon();
        this.parentsId = model.getParent() == null ? 0 : model.getParent().getId();
        this.level = model.getLevel();
        this.order = model.getOrderNum();
    }
}
