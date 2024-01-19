package com.erp.base.model.dto.request.client;

import com.erp.base.model.ClientIdentity;
import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.model.entity.ClientModel;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest implements IBaseDto<ClientModel> {
    @NotBlank(message = "client.userNameNotEmpty")
    private String username;
    private String password = username;
    private Long roleId;

    public ClientModel toModel() {
        ClientModel model = new ClientModel();
        model.setUsername(username);
        model.setPassword(username);
        model.setCreateBy(ClientIdentity.getUser().getId());
        model.setMustUpdatePassword(true);
        Set<RoleModel> role = model.getRoles();
        //default 1
        role.add(new RoleModel((Long)Objects.requireNonNullElse(roleId, 1)));
        return model;
    }

    @Override
    public Specification<ClientModel> getSpecification() {
        GenericSpecifications<ClientModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("name", "like", username)
                .build();
    }
}
