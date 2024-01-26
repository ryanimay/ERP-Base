package com.erp.base.enums.cache;
/**
 * cache組的相關設定
 * */
public enum CacheEnum {
    CLIENT(CacheConstant.CLIENT.NAME_CLIENT, 30),
    ROLE_PERMISSION(CacheConstant.ROLE_PERMISSION.NAME_ROLE_PERMISSION, 60)
    ;

    private final String enumName;
    private final int ttl;//min

    CacheEnum(String enumName, int ttl) {
        this.enumName = enumName;
        this.ttl = ttl;
    }

    public String getEnumName() {
        return enumName;
    }

    public int getTtl() {
        return ttl;
    }
}
