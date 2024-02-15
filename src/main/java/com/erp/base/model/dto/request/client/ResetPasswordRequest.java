package com.erp.base.model.dto.request.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "重設密碼請求")
public class ResetPasswordRequest {
    @NotBlank(message = "client.userNameNotEmpty")
    @Schema(description = "用戶名")
    private String username;
    @NotBlank(message = "client.emailNotEmpty")
    @Email(message = "client.invalidEmailFormat")
    @Schema(description = "用戶mail")
    private String email;
}
