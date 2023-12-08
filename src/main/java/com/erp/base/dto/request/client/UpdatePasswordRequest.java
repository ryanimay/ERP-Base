package com.erp.base.dto.request.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdatePasswordRequest extends LoginRequest{

    @NotBlank(message = "client.passwordNotEmpty")
    private String oldPassword;

    @NotBlank(message = "client.passwordNotEmpty")
    @Size(min = 8, max = 20, message = "client.passwordSize")
    @Pattern.List({
            @Pattern(regexp = ".*[a-z].*", message = "client.passwordContainLowercaseL"),
            @Pattern(regexp = ".*[A-Z].*", message = "client.passwordContainUppercaseL"),
            @Pattern(regexp = ".*\\d.*", message = "client.passwordContainNumber"),
            @Pattern(regexp = "^[^\\s!@#$%^&*()_+={}\\[\\]:;<>,.?~\\\\/-]+$", message = "client.passwordNotContainSpecialCharacters")
    })
    private String password;
}
