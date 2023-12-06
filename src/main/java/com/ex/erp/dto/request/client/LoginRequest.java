package com.ex.erp.dto.request.client;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "client.userNameNotEmpty")
    private String username;
    @NotBlank(message = "client.passwordNotEmpty")
    private String password;
}
