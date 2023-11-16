package com.ex.erp.model;

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
    @Column(name = "roleId")
    private int roleId;
    @Column(name = "is_active", nullable = false, columnDefinition = "BIT DEFAULT 1")
    private boolean isActive;
    @Column(name = "is_lock", nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isLock;
}
