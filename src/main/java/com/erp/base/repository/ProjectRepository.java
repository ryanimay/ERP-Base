package com.erp.base.repository;

import com.erp.base.model.entity.ProjectModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectModel, Long>, JpaSpecificationExecutor<ProjectModel> {
    @Modifying
    @Query("UPDATE ProjectModel p SET p.status = :statusApproved, p.startTime = :now WHERE p.status = :statusPending AND p.id = :projectId")
    int start(Long projectId, LocalDateTime now, int statusPending, int statusApproved);
    @Modifying
    @Query("UPDATE ProjectModel p SET p.status = :statusClosed, p.endTime = :now WHERE p.status = :statusApproved AND p.id = :projectId")
    int done(Long projectId, LocalDateTime now, int statusApproved, int statusClosed);
    @Modifying
    @Query("UPDATE ProjectModel p SET p.orderNum = :order WHERE p.id = :id")
    void updateOrder(String id, Integer order);
}
