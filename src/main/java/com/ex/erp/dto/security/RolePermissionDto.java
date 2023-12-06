package com.ex.erp.dto.security;

import com.ex.erp.model.PermissionModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

/**
 * 角色驗證用的權限DTO，只留簡單資料
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionDto implements GrantedAuthority, Serializable {
    private long id;
    private String authority;
    private String info;
    private String url;
    private long parentId;

    public RolePermissionDto(PermissionModel model) {
        this.id = model.getId();
        this.authority = model.getAuthority();
        this.info = model.getInfo();
        this.url = model.getUrl();
        this.parentId = model.getParentId();
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
