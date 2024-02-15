package com.erp.base.model.dto.request.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "禁用權限請求")
public class BanRequest {
    @Schema(description = "權限ID")
    private long id;
    @Schema(description = "狀態")
    private boolean status;
}
