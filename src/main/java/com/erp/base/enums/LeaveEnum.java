package com.erp.base.enums;

public enum LeaveEnum {
    ANNUAL("Annual Leave"),
    SICK("Sick Leave"),
    MATERNITY("Maternity Leave"),
    PATERNITY("Paternity Leave"),
    MARRIAGE("Marriage Leave"),
    BEREAVEMENT("Bereavement Leave"),
    PUBLIC("Public Holiday Leave"),
    ;
    private String name;
    LeaveEnum(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
