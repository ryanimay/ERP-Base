package com.erp.base.model.entity;

import com.erp.base.enums.RoleConstant;
import com.erp.base.model.dto.security.RolePermissionDto;
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
    @Column(name = "role_name", nullable = false)
    private String roleName;
    @Column(name = "level", nullable = false)
    private int level = RoleConstant.LEVEL_0;

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
