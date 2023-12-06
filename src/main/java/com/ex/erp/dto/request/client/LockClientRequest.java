package com.ex.erp.dto.request.client;

import lombok.Data;

@Data
public class LockClientRequest {
    private long clientId;
    private boolean status;
}
