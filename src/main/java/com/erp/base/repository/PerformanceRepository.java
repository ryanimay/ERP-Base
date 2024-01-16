package com.erp.base.repository;

import com.erp.base.model.entity.PerformanceModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceModel, Long> {
    @Query("SELECT P FROM PerformanceModel p " +
            "WHERE (:userId IS NULL OR p.user.id = :userId) " +
            "AND p.createTime >= :startTime " +
            "AND p.createTime <= :endTime")
    List<PerformanceModel> findAllPerformance(Long userId, LocalDateTime startTime, LocalDateTime endTime, PageRequest page);
    @Query("UPDATE PerformanceModel p SET p.status = 4 WHERE p.id = :eventId")
    void updateStateRemoved(Long eventId);
    @Query("UPDATE PerformanceModel p SET p.status = 2 WHERE p.id = :eventId")
    void updateStateAccept(Long eventId);
}
