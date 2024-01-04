package com.erp.base.model;

import com.erp.base.dto.security.RolePermissionDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleModel implements IBaseModel {
    @Serial
    private static final long serialVersionUID = -5831980508981736029L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "roleName", nullable = false)
    private String roleName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<PermissionModel> permissions = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<RouterModel> routers = new HashSet<>();

    public RoleModel(String roleName) {
        this.roleName = roleName;
    }

    public RoleModel(long id) {
        this.id = id;
    }

    //存簡單資料就好，剔除父類
    public Set<RolePermissionDto> getRolePermissionsDto() {
        Set<PermissionModel> permissionSet = getPermissions();
        Set<RolePermissionDto> dtoResult = new HashSet<>();
        permissionSet.forEach(model -> dtoResult.add(new RolePermissionDto(model)));
        return dtoResult;
    }
}
