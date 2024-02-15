package com.erp.base.model.dto.request.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新用戶請求")
public class UpdateClientInfoRequest {
    @NotBlank(message = "client.userNameNotEmpty")
    @Schema(description = "用戶名")
    private String username;

    @NotBlank(message = "client.emailNotEmpty")
    @Email(message = "client.invalidEmailFormat")
    @Schema(description = "用戶mail")
    private String email;
    @Schema(description = "用戶角色")
    private List<Long> roles;
    @Schema(description = "用戶部門")
    private Long departmentId;
}
