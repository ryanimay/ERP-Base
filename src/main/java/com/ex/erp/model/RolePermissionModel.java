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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private RoleModel role;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permission_id")
    private PermissionModel permission;
}
