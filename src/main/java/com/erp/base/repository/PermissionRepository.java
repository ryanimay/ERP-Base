package com.erp.base.repository;

import com.erp.base.model.PermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionModel, Long> {
    @Modifying
    @Query("UPDATE PermissionModel p SET p.status = :status WHERE p.id = :id")
    void updateStatusById(long id, boolean status);
}
