package com.ex.erp.dto;

import com.ex.erp.model.ClientModel;

public class ClientRegisterDto implements IBaseDto<ClientModel> {
    private String username;
    private String password;

    public ClientRegisterDto() {
    }

    public ClientRegisterDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ClientModel toModel() {
        ClientModel model = new ClientModel();
        model.setUsername(username);
        model.setPassword(password);
        return model;
    }
}
