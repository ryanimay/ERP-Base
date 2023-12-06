package com.ex.erp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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

    @Transient
    private Set<String> authoritiesIncludeParents;

    //根節點的parentsId設為0L，後續比較好整理
    public Long getParentId() {
        PermissionModel parent = getParentPermission();
        if(parent == null) return 0L;
        return parent.getId();
    }

    public void setAuthoritiesIncludeParents(Set<String> getAuthoritiesIncludeParents) {
//        另一種方法:
//        if(getAuthoritiesIncludeParents == null) return;
//        if (this.authoritiesIncludeParents != null) {
//            this.authoritiesIncludeParents.addAll(getAuthoritiesIncludeParents);
//        }else{
//            this.authoritiesIncludeParents = new HashSet<>(getAuthoritiesIncludeParents)
//        }

        if (this.authoritiesIncludeParents != null && getAuthoritiesIncludeParents != null) {
            this.authoritiesIncludeParents.addAll(getAuthoritiesIncludeParents);
        }
    }

    public Set<String> getAuthoritiesIncludeParents() {
        if(this.authoritiesIncludeParents == null){
            this.authoritiesIncludeParents = new HashSet<>();
            this.authoritiesIncludeParents.add(this.authority);
        }
        return authoritiesIncludeParents;
    }
}
