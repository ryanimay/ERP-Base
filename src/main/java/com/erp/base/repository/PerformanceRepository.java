package com.erp.base.repository;

import com.erp.base.model.entity.PerformanceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceModel, Long>, JpaSpecificationExecutor<PerformanceModel> {
    @Modifying
    @Query("UPDATE PerformanceModel p SET p.status = :updateStatus WHERE p.id = :eventId AND p.status = :status")
    int updateStatus(Long eventId, int status, int updateStatus);
    @Query("SELECT p FROM PerformanceModel p WHERE p.status = :status")
    Page<PerformanceModel> findAllByStatus(int status, PageRequest page);
    @Query("SELECT p FROM PerformanceModel p WHERE p.status = :status AND p.user.department.name = :departmentName")
    Page<PerformanceModel> findByStatusAndDepartment(String departmentName, int status, PageRequest page);
}
