package com.ex.erp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")//防止多個子節點重複序列化同一父節點
public class PermissionModel implements IBaseModel{
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

    @ManyToOne
    @JoinColumn(name = "parentsId")
    @JsonIgnoreProperties("parentPermission")
    private PermissionModel parentPermission;

    //根節點的parentsId設為0L，後續比較好整理
    public Long getParentId() {
        PermissionModel parent = getParentPermission();
        if(parent == null) return 0L;
        return parent.getId();
    }
}
