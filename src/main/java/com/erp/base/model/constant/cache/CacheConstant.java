package com.erp.base.model.constant.cache;
/**
 * CacheName常量
 * */
public interface CacheConstant {
    String SPLIT_CONSTANT = ":";
    interface CLIENT{
        String NAME_CLIENT = "client";
        String CLIENT = "client_";
        String CLIENT_NAME_LIST = "clientNameList";
        String SYSTEM_USER = "systemUser";
    }

    interface ROLE_PERMISSION{
        String NAME_ROLE_PERMISSION = "rolePermission";
        String ROLES = "roles";
        String PERMISSIONS = "permissions";
        String PERMISSIONS_MAP = "permissionMap";
        String ROLE_PERMISSION = "rolePermission_";
        String PERMISSION_STATUS = "permissionStatus_";
        String DEPARTMENT = "department_";
        String MENU_TREE = "menuTree";
        String ROLE_MENU = "roleMenu_";
    }

    interface TOKEN_BLACK_LIST{
        String TOKEN_BLACK_LIST = "tokenBlackList";
    }
    interface OTHER{
        String OTHER = "other";
        String SYSTEM_DEPARTMENT = "systemDepartment";
        String SYSTEM_PROJECT = "systemProject";
        String SYSTEM_PROCURE = "systemProcure";
    }
}
