package com.erp.base.model.dto.request.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "登入請求")
public class LoginRequest {
    @NotBlank(message = "client.userNameNotEmpty")
    @Schema(description = "用戶名")
    private String username;
    @NotBlank(message = "client.passwordNotEmpty")
    @Schema(description = "用戶密碼")
    private String password;
}
