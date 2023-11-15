package com.ex.erp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class RoleModel implements IBaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "roleName", nullable = false)
    private String roleName;
    @Column(name = "permissionId", nullable = false)
    private int permissionId;

    public RoleModel() {
    }

    public RoleModel(String roleName, int permissionId) {
        this.roleName = roleName;
        this.permissionId = permissionId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }
}
