package com.erp.base.enums;

public enum CacheEnum {
    CLIENT("client", 30)
    ;

    private String enumName;
    private int ttl;//min

    CacheEnum(String enumName, int ttl) {
        this.enumName = enumName;
        this.ttl = ttl;
    }

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
