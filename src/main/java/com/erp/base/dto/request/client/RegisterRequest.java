package com.erp.base.dto.request.client;

import com.erp.base.dto.request.IBaseDto;
import com.erp.base.dto.security.ClientIdentity;
import com.erp.base.model.RoleModel;
import com.erp.base.model.UserModel;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest implements IBaseDto<UserModel> {
    @NotBlank(message = "client.userNameNotEmpty")
    private String username;
    private String password = username;
    private Long roleId;

    public UserModel toModel() {
        UserModel model = new UserModel();
        model.setUsername(username);
        model.setPassword(username);
        model.setCreateBy(ClientIdentity.getUser().getId());
        Set<RoleModel> role = model.getRoles();
        //default 1
        role.add(new RoleModel((Long)Objects.requireNonNullElse(roleId, 1)));
        return model;
    }
}
