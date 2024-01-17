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
        String REMOVE = ROLE + "/remove";
        String ROLE_PERMISSION = ROLE + "/rolePermission";
        String ROLE_ROUTER = ROLE + "/roleRouter";
    }

    interface PERMISSION{
        String PERMISSION = "/permission";
        String ROLE = PERMISSION + "/role";
        String LIST = PERMISSION + "/list";
        String BAN = PERMISSION + "/ban";
        String SECURITY_CONFIRM = PERMISSION + "/securityConfirm";
    }

    interface ROUTER{
        String ROUTER = "/router";
        String CONFIG_LIST = ROUTER + "/configList";
        String LIST = ROUTER + "/list";
        String ROLE = ROUTER + "/role";
    }

    interface SALARY{
        String SALARY = "/salary";
        String ROOTS = SALARY + "/roots";
        String ROOT_BY = SALARY + "/rootBy";
        String EDIT_ROOT = SALARY + "/editRoot";
        String GET = SALARY + "/get";
        String INFO = SALARY + "/info";
    }

    interface PERFORMANCE{
        String PERFORMANCE = "/performance";
        String ALL_LIST = PERFORMANCE + "/allList";
        String LIST = PERFORMANCE + "/list";
        String ADD = PERFORMANCE + "/add";
        String UPDATE = PERFORMANCE + "/update";
        String REMOVE = PERFORMANCE + "/remove";
        String ACCEPT = PERFORMANCE + "/accept";
    }

    interface ATTEND{
        String ATTEND = "/attend";
        String SIGN_IN = ATTEND + "/signIn";
        String SIGN_OUT = ATTEND + "/signOut";
    }

    interface LEAVE{
        String LEAVE = "/leave";
        String PENDING_LIST = LEAVE + "/pendingList";
        String LIST = LEAVE + "/list";
        String ADD = LEAVE + "/add";
        String UPDATE = LEAVE + "/update";
        String DELETE = LEAVE + "/delete";
        String ACCEPT = LEAVE + "/accept";
    }
}
