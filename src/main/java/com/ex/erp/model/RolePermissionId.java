package com.ex.erp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionId implements IBaseModel {

    private Long role;

    private Long permission;
}
