package com.erp.base.model.dto.request.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新用戶請求")
public class UpdateClientInfoRequest {
    @NotNull(message = "client.userIdNotEmpty")
    @Schema(description = "用戶id")
    private Long id;
    @Schema(description = "用戶名")
    private String username;
    @Email(message = "client.invalidEmailFormat")
    @Schema(description = "用戶mail")
    private String email;
    @Schema(description = "用戶角色")
    private List<Long> roles;
    @Schema(description = "用戶部門")
    private Long departmentId;
    @Schema(description = "用戶年假總數")
    private String annualLeave;
}
