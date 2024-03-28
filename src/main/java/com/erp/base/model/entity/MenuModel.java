package com.erp.base.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

/**
 * 菜單路由
 */
@Entity
@Table(name = "menu")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuModel implements IBaseModel {
    @Serial
    private static final long serialVersionUID = -5L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "path")
    private String path;//對應routerName
    @Column(name = "icon")
    private String icon;//前端elementPlus icon
    @Column(name = "level")
    private int level;
    @Column(name = "order_num")
    private int orderNum;
    @Column(name = "status")
    private boolean status = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "menu_role",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleModel> roles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private MenuModel parent;

    public MenuModel(Long id) {
        this.id = id;
    }
}
