package com.erp.base.model.dto.response;

import com.erp.base.model.entity.ClientModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ClientNameRoleObject extends ClientNameObject{
    private int level;

    public ClientNameRoleObject(ClientModel model, int level) {
        super(model);
        this.level = level;
    }
}
