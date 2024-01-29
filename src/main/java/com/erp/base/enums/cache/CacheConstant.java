package com.erp.base.enums.cache;
/**
 * CacheName常量
 * name不用加「'」
 * key要加「'」做為SpEL
 * */
public interface CacheConstant {
    interface CLIENT{
        String NAME_CLIENT = "client";
        String CLIENT = "'client_'";
        String CLIENT_NAME_LIST = "'clientNameList'";
    }

    interface ROLE_PERMISSION{
        String NAME_ROLE_PERMISSION = "rolePermission";
        String ROLES = "'roles'";
        String PERMISSIONS = "'permissions'";
        String ROUTERS = "'routers'";
        String PERMISSIONS_MAP = "'permissionMap'";
        String ROLE_PERMISSION = "'rolePermission_'";
        String ROLE_ROUTER = "'roleRouter_'";
        String PERMISSION_STATUS = "'permissionStatus_'";
        String DEPARTMENT = "'department_'";
    }
}
