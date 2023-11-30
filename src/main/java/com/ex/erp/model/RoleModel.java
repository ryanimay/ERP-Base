package com.ex.erp.model;

import com.ex.erp.dto.security.PermissionDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleModel implements IBaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "roleName", nullable = false)
    private String roleName;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<PermissionModel> permissions = new HashSet<>();

    public RoleModel(long id) {
        this.id = id;
    }

    //存簡單資料就好，剔除父類
    public Set<PermissionDto> getPermissionsDto() {
        Set<PermissionModel> permissionSet = getPermissions();
        Set<PermissionDto> dtoResult = new HashSet<>();
        permissionSet.forEach(model -> dtoResult.add(new PermissionDto(model)));
        return dtoResult;
    }
}
