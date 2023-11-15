package com.ex.erp.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
@Entity
@Table(name = "permission")
public class PermissionModel implements GrantedAuthority, IBaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "permissionName", nullable = false)
    private String permissionName;

    public PermissionModel() {
    }

    public PermissionModel(int id, String permissionName) {
        this.id = id;
        this.permissionName = permissionName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    @Override
    public String getAuthority() {
        return this.permissionName;
    }
}
