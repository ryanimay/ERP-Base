package com.ex.erp.repository;

import com.ex.erp.model.PermissionModel;
import com.ex.erp.model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<RoleModel, Long> {
    @Query("SELECT rp.permission FROM RolePermissionModel rp WHERE rp.role.id = :roleId")
    Set<PermissionModel> getPermissionsByRoleId(@Param("roleId") Long roleId);
}
