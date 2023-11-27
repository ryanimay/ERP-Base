package com.ex.erp.dto.response;

import com.ex.erp.model.ClientModel;
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
public class ClientResponseModel implements Serializable {
    private long id;
    private String username;
    private long roleId;
    private boolean isActive;
    private boolean isLock;

    public ClientResponseModel(ClientModel clientModel) {
        this.id = clientModel.getId();
        this.username = clientModel.getUsername();
        this.roleId = clientModel.getRole().getId();
        this.isActive = clientModel.isActive();
        this.isLock = clientModel.isLock();
    }
}
