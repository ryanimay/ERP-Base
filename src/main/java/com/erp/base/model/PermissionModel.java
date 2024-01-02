package com.erp.base.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionModel implements IBaseModel {
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
    @Column(name = "status", nullable = false)
    private Boolean status = true;
}
