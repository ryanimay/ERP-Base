package com.erp.base.repository;

import com.erp.base.model.entity.PerformanceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceModel, Long> {
    @Query("SELECT p FROM PerformanceModel p " +
            "WHERE (:userId IS NULL OR p.user.id = :userId) " +
            "AND p.createTime >= :startTime " +
            "AND p.createTime <= :endTime")
    Page<PerformanceModel> findAllPerformance(Long userId, LocalDateTime startTime, LocalDateTime endTime, PageRequest page);
    @Modifying
    @Query("UPDATE PerformanceModel p SET p.status = 4 WHERE p.id = :eventId AND p.status = 1")
    int updateStateRemoved(Long eventId);
    @Modifying
    @Query("UPDATE PerformanceModel p SET p.status = 2 WHERE p.id = :eventId AND p.status = 1")
    int updateStateAccept(Long eventId);
    @Query("SELECT p FROM PerformanceModel p WHERE p.status = 1")
    Page<PerformanceModel> findAllByStatus(PageRequest page);
}
