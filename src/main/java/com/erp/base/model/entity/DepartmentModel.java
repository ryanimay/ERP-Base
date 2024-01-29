package com.erp.base.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 部門
 * */
@Entity
@Table(name = "department")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "clientModelList")
public class DepartmentModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<ClientModel> clientModelList;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @JsonIgnore
    private RoleModel default_role; //預設權限

    public DepartmentModel(Long departmentId) {
        this.id = departmentId;
    }
}
