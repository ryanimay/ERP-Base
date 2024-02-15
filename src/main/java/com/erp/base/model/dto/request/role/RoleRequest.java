package com.erp.base.model.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色請求")
public class RoleRequest {
    @Schema(description = "角色ID")
    private Long id;
    @Schema(description = "角色名稱")
    private String name;
}
