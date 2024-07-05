package com.erp.base.repository;

import com.erp.base.model.entity.DepartmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentModel, Long>, JpaSpecificationExecutor<DepartmentModel> {
    @Modifying
    @Query(value = "DELETE FROM department_role WHERE role_id = :id", nativeQuery = true)
    void removeRole(Long id);
}
