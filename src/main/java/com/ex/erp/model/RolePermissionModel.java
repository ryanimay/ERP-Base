package com.ex.erp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role_permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(RolePermissionId.class)
public class RolePermissionModel implements IBaseModel{
    @Id
    @ManyToOne
    @JoinColumn(name = "roleId", referencedColumnName = "id")
    private RoleModel role;

    @Id
    @ManyToOne
    @JoinColumn(name = "permissionId", referencedColumnName = "id")
    private PermissionModel permission;
}
