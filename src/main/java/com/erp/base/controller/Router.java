package com.erp.base.controller;

public interface Router {
    interface CACHE{
        String CACHE = "/cache";
        String REFRESH = CACHE + "/refresh";
    }

    interface  CLIENT{
        String CLIENT = "/client";
        String OP_VALID = CLIENT + "/opValid";
        String REGISTER = CLIENT + "/register";
        String LOGIN = CLIENT + "/login";
        String RESET_PASSWORD = CLIENT + "/resetPassword";
        String LIST = CLIENT + "/list";
        String GET_CLIENT = CLIENT + "/getClient";
        String UPDATE = CLIENT + "/update";
        String UPDATE_PASSWORD = CLIENT + "/updatePassword";
        String CLIENT_LOCK = CLIENT + "/clientLock";
        String CLIENT_STATUS = CLIENT + "/clientStatus";
    }

    interface ROLE{
        String ROLE = "/role";
        String LIST = ROLE + "/list";
        String UPDATE = ROLE + "/update";
        String ADD = ROLE + "/add";
        String ROLE_PERMISSION = ROLE + "/rolePermission";
        String REMOVE = ROLE + "/remove";
    }

    interface PERMISSION{
        String PERMISSION = "/permission";
        String ROLE = PERMISSION + "/role";
        String LIST = PERMISSION + "/list";
        String BAN = PERMISSION + "/ban";
        String SECURITY_CONFIRM = PERMISSION + "/securityConfirm";
    }

}
