package com.ex.erp.dto.request;

import com.ex.erp.model.ClientModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest implements IBaseDto<ClientModel> {
    @NotBlank(message = "用戶名不得為空")
    @Size(min = 6, max = 20, message = "用戶名稱長度不得小於{min}, 不得大於{max}")
    private String username;
    @NotBlank(message = "密碼不得為空")
    @Size(min = 8, max = 20, message = "密碼長度不得小於{min}, 不得大於{max}")
    @Pattern.List({
            @Pattern(regexp = ".*[a-z].*", message = "密碼必須包含小寫字母"),
            @Pattern(regexp = ".*[A-Z].*", message = "密碼必須包含大寫字母"),
            @Pattern(regexp = ".*\\d.*", message = "密碼必須包含數字"),
            @Pattern(regexp = "^[^\\s!@#$%^&*()_+={}\\[\\]:;<>,.?~\\\\/-]+$", message = "密碼不得包含空格或特殊字符")
    })
    private String password;

    public ClientModel toModel() {
        ClientModel model = new ClientModel();
        model.setUsername(username);
        model.setPassword(password);
        return model;
    }
}
