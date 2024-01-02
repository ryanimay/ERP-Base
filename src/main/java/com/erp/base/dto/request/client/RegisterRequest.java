package com.erp.base.dto.request.client;

import com.erp.base.dto.request.IBaseDto;
import com.erp.base.model.ClientModel;
import com.erp.base.model.RoleModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest implements IBaseDto<ClientModel> {
    @NotBlank(message = "client.userNameNotEmpty")
    @Size(min = 6, max = 20, message = "client.userNameSize")
    private String username;
    @NotBlank(message = "client.passwordNotEmpty")
    @Size(min = 8, max = 20, message = "client.passwordSize")
    @Pattern.List({
            @Pattern(regexp = ".*[a-z].*", message = "client.passwordContainLowercaseL"),
            @Pattern(regexp = ".*[A-Z].*", message = "client.passwordContainUppercaseL"),
            @Pattern(regexp = ".*\\d.*", message = "client.passwordContainNumber"),
            @Pattern(regexp = "^[^\\s!@#$%^&*()_+={}\\[\\]:;<>,.?~\\\\/-]+$", message = "client.passwordNotContainSpecialCharacters")
    })
    private String password;

    @NotBlank(message = "client.emailNotEmpty")
    @Email(message = "client.invalidEmailFormat")
    private String email;
    private Long roleId;

    public ClientModel toModel() {
        ClientModel model = new ClientModel();
        model.setUsername(username);
        model.setPassword(password);
        model.setEmail(email);
        if(roleId != null){
            Set<RoleModel> role = model.getRoles();
            role.add(new RoleModel(roleId));
        }
        return model;
    }
}
