package com.erp.base.model.dto.response;

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
}
