package com.erp.base.enums;

public enum JobStatusEnum {
    PENDING("Pending"),
    APPROVED("Approved"),
    CLOSED("Closed"),
    REMOVED("Removed"),
    ;
    private String name;
    JobStatusEnum(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
