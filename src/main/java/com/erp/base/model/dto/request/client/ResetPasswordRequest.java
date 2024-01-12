package com.erp.base.model.dto.request.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "client.userNameNotEmpty")
    private String username;
    @NotBlank(message = "client.emailNotEmpty")
    @Email(message = "client.invalidEmailFormat")
    private String email;
}
