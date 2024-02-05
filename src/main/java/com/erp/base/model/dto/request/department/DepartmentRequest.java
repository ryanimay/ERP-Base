package com.erp.base.model.dto.request.department;

import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.RoleModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@Data
@EqualsAndHashCode(callSuper = false)
public class DepartmentRequest extends PageRequestParam implements IBaseDto<DepartmentModel> {
    private Long id;
    private String name;
    private Long defaultRoleId;


    @Override
    public DepartmentModel toModel() {
        DepartmentModel model = new DepartmentModel();
        model.setName(name);
        model.setDefaultRole(new RoleModel(defaultRoleId));
        return model;
    }

    @Override
    public Specification<DepartmentModel> getSpecification() {
        GenericSpecifications<DepartmentModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("id", GenericSpecifications.EQ, id)
                .add("name", GenericSpecifications.LIKE, name)
                .build();
    }
}
