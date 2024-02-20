package com.erp.base.model.dto.request.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用戶更新密碼請求")
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {
    @NotNull(message = "client.userIdNotEmpty")
    @Schema(description = "用戶id")
    private Long id;

    @NotBlank(message = "client.passwordNotEmpty")
    @Schema(description = "舊密碼")
    private String oldPassword;

    @NotBlank(message = "client.passwordNotEmpty")
    @Size(min = 8, max = 20, message = "client.passwordSize")
    @Pattern.List({
            @Pattern(regexp = ".*[a-z].*", message = "client.passwordContainLowercaseL"),
            @Pattern(regexp = ".*[A-Z].*", message = "client.passwordContainUppercaseL"),
            @Pattern(regexp = ".*\\d.*", message = "client.passwordContainNumber"),
            @Pattern(regexp = "^[^\\s!@#$%^&*()_+={}\\[\\]:;<>,.?~\\\\/-]+$", message = "client.passwordNotContainSpecialCharacters")
    })
    @Schema(description = "新密碼")
    private String password;
}
