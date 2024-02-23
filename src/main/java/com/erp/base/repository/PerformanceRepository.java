package com.erp.base.repository;

import com.erp.base.model.entity.PerformanceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceModel, Long>, JpaSpecificationExecutor<PerformanceModel> {
    @Modifying
    @Query("UPDATE PerformanceModel p SET p.status = :updateStatus WHERE p.id = :eventId AND p.status = :status")
    int updateStatus(Long eventId, int status, int updateStatus);
    @Query("SELECT p FROM PerformanceModel p WHERE p.status = :status AND p.user.id <> :id")
    Page<PerformanceModel> findAllByStatus(int status, long id, PageRequest page);
    @Query("SELECT p FROM PerformanceModel p WHERE p.status = :status AND p.user.department.name = :departmentName AND p.user.id <> :id")
    Page<PerformanceModel> findByStatusAndDepartment(String departmentName, int status, long id, PageRequest page);
    @Query("SELECT p.user, SUM(p.fixedBonus), SUM(p.performanceRatio), FUNCTION('YEAR', CURRENT_DATE) AS settleYear, COUNT(p) FROM PerformanceModel p " +
            "WHERE (:userId IS NULL OR p.user.id = :userId) " +
            "AND p.status = :status " +
            "AND FUNCTION('YEAR', p.createTime) = FUNCTION('YEAR', CURRENT_DATE) " +
            "GROUP BY p.user")
    Set<Object[]> calculateByCreateYear(Long userId, int status);
}
