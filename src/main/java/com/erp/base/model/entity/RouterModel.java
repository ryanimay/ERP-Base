package com.erp.base.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 前端路由
 */
@Entity
@Table(name = "router")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouterModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "path")
    private String path;
    @Column(name = "name")
    private String name;
    @Column(name = "components")
    private String components;
    @Column(name = "metas")
    private String metas;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "router_role",
            joinColumns = @JoinColumn(name = "router_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleModel> roles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private RouterModel parent;

    public RouterModel(Long id) {
        this.id = id;
    }
}
