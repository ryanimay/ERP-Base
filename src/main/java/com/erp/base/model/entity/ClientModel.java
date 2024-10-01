package com.erp.base.model.entity;

import com.erp.base.tool.DateTool;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = "id")
public class ClientModel implements IBaseModel, Comparable<ClientModel> {
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
    private LocalDateTime createTime = DateTool.now();
    @Column(name = "create_by")
    private long createBy;
    @Column(name = "must_update_password")
    private boolean mustUpdatePassword = false;
    @Column(name = "attend_status")
    private int attendStatus = 1;//1.未打卡 2.已簽到 3.已簽退

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "client_roles",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleModel> roles = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private DepartmentModel department;//所屬部門

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "annual_leave_id")
    private AnnualLeaveModel annualLeave;//個人年假計算

    public ClientModel(long id) {
        this.id = id;
    }

    @Override
    public int compareTo(ClientModel model) {
        return Long.compare(id, model.getId());
    }
}
