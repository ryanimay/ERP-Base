package com.erp.base.repository;

import com.erp.base.model.entity.PermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionModel, Long> {
    @Modifying
    @Query("UPDATE PermissionModel p SET p.status = :status WHERE p.id = :id")
    void updateStatusById(long id, boolean status);
    @Query("SELECT p.status FROM PermissionModel p WHERE p.url = :requestUrl")
    Boolean checkPermissionIfDeny(String requestUrl);
}
