package com.erp.base.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "client")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientModel implements IBaseModel {
    @Serial
    private static final long serialVersionUID = 1L;
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
    @Column(name = "email")
    private String email;
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
    @Column(name = "create_by")
    private long createBy;
    @Column(name = "must_update_password")
    private boolean mustUpdatePassword = false;
    @Column(name = "attend_status")
    private int attendStatus = 1;//1.未打卡 2.已簽到 3.已簽退

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_roles",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleModel> roles = new HashSet<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_notifications",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "notifications_id"))
    private Set<NotificationModel> notifications = new HashSet<>();

    public ClientModel(long id) {
        this.id = id;
    }
}
