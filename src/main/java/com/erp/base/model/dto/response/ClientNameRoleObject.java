package com.erp.base.model.dto.response;

import com.erp.base.model.dto.response.role.RoleNameResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.RoleModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class ClientNameRoleObject extends ClientNameObject{
    private List<RoleNameResponse> roles = new ArrayList<>();

    public ClientNameRoleObject(ClientModel model) {
        super(model);
        Set<RoleModel> roleSet = model.getRoles();
        List<RoleModel> roleList = roleSet.stream().sorted(Comparator
                .comparingInt(RoleModel::getLevel).reversed()
                .thenComparing(RoleModel::getId)).toList();
        roleList.forEach(role -> this.roles.add(new RoleNameResponse(role)));

    }
}
