package com.ex.erp.dto.request;

import com.ex.erp.model.ClientModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClientInfoRequest implements IBaseDto<ClientModel> {
    @NotBlank(message = "client.userNameNotEmpty")
    @Size(min = 6, max = 20, message = "client.userNameSize")
    private String username;

    @NotBlank(message = "client.emailNotEmpty")
    @Email(message = "client.invalidEmailFormat")
    private String email;

    public ClientModel toModel() {
        ClientModel model = new ClientModel();
        model.setUsername(username);
        model.setEmail(email);
        return model;
    }
}
