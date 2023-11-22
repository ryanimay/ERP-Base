package com.ex.erp.dto.response;

import com.ex.erp.model.ClientModel;
import com.ex.erp.model.RoleModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * 使用者資訊，不存密碼
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse implements Serializable {
    private long id;
    private String username;
    private RoleModel roleModel;
    private boolean isActive;
    private boolean isLock;

    public ClientResponse(ClientModel clientModel) {
        this.id = clientModel.getId();
        this.username = clientModel.getUsername();
        this.roleModel = clientModel.getRole();
        this.isActive = clientModel.isActive();
        this.isLock = clientModel.isLock();
    }
}
