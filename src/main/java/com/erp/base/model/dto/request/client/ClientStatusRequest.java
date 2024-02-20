package com.erp.base.model.dto.request.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "用戶狀態請求")
@NoArgsConstructor
@AllArgsConstructor
public class ClientStatusRequest {
    @Schema(description = "用戶ID")
    private long clientId;
    @Schema(description = "用戶名")
    private String username;
    @Schema(description = "用戶狀態")
    private boolean status;
}
