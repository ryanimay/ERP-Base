package com.erp.base.model.dto.request.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClientInfoRequest {
    @NotBlank(message = "client.userNameNotEmpty")
    private String username;

    @NotBlank(message = "client.emailNotEmpty")
    @Email(message = "client.invalidEmailFormat")
    private String email;

    private List<Long> roles;
}
