package com.ex.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "用戶名不得為空")
    private String username;
    @NotBlank(message = "密碼不得為空")
    private String password;
}
