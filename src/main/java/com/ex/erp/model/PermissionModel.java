package com.ex.erp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
@Entity
@Table(name = "permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionModel implements GrantedAuthority, IBaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "authority", nullable = false)
    private String authority;

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
