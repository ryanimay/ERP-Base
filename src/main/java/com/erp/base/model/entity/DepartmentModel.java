package com.erp.base.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部門
 * */
@Entity
@Table(name = "department")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String name;
}
