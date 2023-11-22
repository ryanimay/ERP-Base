package com.ex.erp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

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
    @Column(name = "info")
    private String info;
    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "parentsId")
    private PermissionModel parentPermission;

    @OneToMany(mappedBy = "parentPermission")
    private Set<PermissionModel> childPermissions = new HashSet<>();

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
