package com.erp.base.model.dto.response;

import com.erp.base.model.entity.ClientModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用者資訊，不存密碼
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseModel implements Serializable {

    private long id;
    private String username;
    private List<Long> roleId = new ArrayList<>();
    private boolean isActive;
    private boolean isLock;
    private String email;
    private boolean mustUpdatePassword;

    public ClientResponseModel(ClientModel clientModel) {
        this.id = clientModel.getId();
        this.username = clientModel.getUsername();
        clientModel.getRoles().forEach(role -> roleId.add(role.getId()));
        this.isActive = clientModel.isActive();
        this.isLock = clientModel.isLock();
        this.email = clientModel.getEmail();
        this.mustUpdatePassword = clientModel.isMustUpdatePassword();
    }
}
