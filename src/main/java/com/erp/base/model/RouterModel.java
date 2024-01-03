package com.erp.base.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
}
