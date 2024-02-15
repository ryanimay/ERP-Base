package com.erp.base.model.dto.request.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "安全認證請求")
public class SecurityConfirmRequest {
    @Schema(description = "安全密碼")
    private String securityPassword;
}
