package com.erp.base.model.dto.response.role;

import com.erp.base.model.entity.PermissionModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionListResponse implements Serializable {
    private String info;
    private List<PermissionModel>  children;
}
