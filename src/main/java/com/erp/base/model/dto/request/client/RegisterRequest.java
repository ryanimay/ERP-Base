package com.erp.base.model.dto.request.client;

import com.erp.base.model.ClientIdentity;
import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.ClientModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "註冊請求")
public class RegisterRequest implements IBaseDto<ClientModel> {
    @NotBlank(message = "client.userNameNotEmpty")
    @Schema(description = "用戶名")
    private String username;
    @Schema(description = "建立人")
    private Long createBy;
    @Schema(description = "部門ID")
    private Long departmentId;

    public ClientModel toModel() {
        ClientModel model = new ClientModel();
        model.setUsername(username);
        model.setPassword(username);
        model.setCreateBy(createBy == null ? Objects.requireNonNull(ClientIdentity.getUser()).getId() : createBy);
        model.setMustUpdatePassword(true);
        return model;
    }

    @Override
    @JsonIgnore
    public Specification<ClientModel> getSpecification() {
        GenericSpecifications<ClientModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("name", GenericSpecifications.LIKE, username)
                .build();
    }
}
