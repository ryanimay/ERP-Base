package com.erp.base.model.dto.request.department;

import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.RoleModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "部門共用請求")
public class DepartmentRequest extends PageRequestParam implements IBaseDto<DepartmentModel> {
    @Schema(description = "部門ID")
    private Long id;
    @Schema(description = "部門名稱")
    private String name;
    @Schema(description = "部門預設權限ID")
    private Long defaultRoleId;


    @Override
    public DepartmentModel toModel() {
        DepartmentModel model = new DepartmentModel();
        if(id != null) model.setId(id);
        model.setName(name);
        model.setDefaultRole(new RoleModel(defaultRoleId));
        return model;
    }

    @Override
    @JsonIgnore
    public Specification<DepartmentModel> getSpecification() {
        GenericSpecifications<DepartmentModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("id", GenericSpecifications.EQ, id)
                .add("name", GenericSpecifications.LIKE, name)
                .build();
    }
}
