package com.erp.base.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 部門
 * */
@Entity
@Table(name = "department")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"clientModelList", "roles"})
public class DepartmentModel implements IBaseModel {
    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ClientModel> clientModelList;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "default_role")
    @JsonIgnore
    private RoleModel defaultRole; //預設權限

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "department_role",
            joinColumns = @JoinColumn(name = "department_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnore
    private Set<RoleModel> roles = new HashSet<>();

    public DepartmentModel(Long departmentId) {
        this.id = departmentId;
    }
}
