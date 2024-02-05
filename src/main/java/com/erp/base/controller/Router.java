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
        String NAME_LIST = CLIENT + "/nameList";
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
        String EDIT_ROOT = SALARY + "/editRoot";
        String GET = SALARY + "/get";
        String INFO = SALARY + "/info";
    }

    interface PERFORMANCE{
        String PERFORMANCE = "/performance";
        String PENDING_LIST = PERFORMANCE + "/pendingList";
        String LIST = PERFORMANCE + "/list";
        String ADD = PERFORMANCE + "/add";
        String UPDATE = PERFORMANCE + "/update";
        String REMOVE = PERFORMANCE + "/remove";
        String ACCEPT = PERFORMANCE + "/accept";
        String CALCULATE = PERFORMANCE + "/calculate";
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
        String TYPE_LIST = LEAVE + "/typeList";
    }

    interface PROJECT{
        String PROJECT = "/project";
        String LIST = PROJECT + "/list";
        String ADD = PROJECT + "/add";
        String UPDATE = PROJECT + "/update";
        String START = PROJECT + "/start";
        String DONE = PROJECT + "/done";
    }

    interface PROCUREMENT{
        String PROCUREMENT = "/procurement";
        String LIST = PROCUREMENT + "/list";
        String ADD = PROCUREMENT + "/add";
        String UPDATE = PROCUREMENT + "/update";
        String DELETE = PROCUREMENT + "/delete";
    }

    interface JOB{
        String JOB = "/job";
        String LIST = JOB + "/list";
        String ADD = JOB + "/add";
        String UPDATE = JOB + "/update";
        String REMOVE = JOB + "/remove";
    }

    interface DEPARTMENT{
        String DEPARTMENT = "/department";
        String LIST = DEPARTMENT + "/list";
        String STAFF = DEPARTMENT + "/staff";
        String EDIT = DEPARTMENT + "/edit";
        String REMOVE = DEPARTMENT + "/remove";
    }
}
