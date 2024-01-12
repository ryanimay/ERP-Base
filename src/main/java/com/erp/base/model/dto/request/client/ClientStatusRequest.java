package com.erp.base.model.dto.request.client;

import lombok.Data;

@Data
public class ClientStatusRequest {
    private long clientId;
    private String username;
    private boolean status;
}
