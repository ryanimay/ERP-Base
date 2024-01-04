package com.erp.base.dto.response;

import com.erp.base.model.UserModel;
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

    public ClientResponseModel(UserModel userModel) {
        this.id = userModel.getId();
        this.username = userModel.getUsername();
        userModel.getRoles().forEach(role -> roleId.add(role.getId()));
        this.isActive = userModel.isActive();
        this.isLock = userModel.isLock();
        this.email = userModel.getEmail();
        this.mustUpdatePassword = userModel.isMustUpdatePassword();
    }
}
