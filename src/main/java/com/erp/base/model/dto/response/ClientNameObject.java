package com.erp.base.model.dto.response;

import com.erp.base.model.entity.ClientModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class ClientNameObject implements Serializable {
    private Long id;
    private String username;

    public ClientNameObject(Object... obj) {
        this.id = (Long) obj[0];
        this.username = (String) obj[1];
    }
    public ClientNameObject(ClientModel model) {
        this.id = model.getId();
        this.username = model.getUsername();
    }
}
