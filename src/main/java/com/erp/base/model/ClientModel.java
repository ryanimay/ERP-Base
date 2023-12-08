package com.erp.base.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="client")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientModel implements IBaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    @Column(name = "is_lock", nullable = false)
    private boolean isLock = false;
    @Column(name = "email", unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "roleId", nullable = false, referencedColumnName = "id")
    private RoleModel role;

    @PrePersist
    public void prePersist(){
        if(role == null) role = defaultRole();
    }

    private RoleModel defaultRole() {
        return new RoleModel(1);
    }
}
