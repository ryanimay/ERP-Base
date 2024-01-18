package com.erp.base.repository;

import com.erp.base.model.entity.ProjectModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectModel, Long> {
    @Query("UPDATE ProjectModel p SET p.status = '3', p.endTime = :now WHERE p.status = '2' AND p.id = :projectId")
    void done(Long projectId, LocalDateTime now);

    @Query("UPDATE ProjectModel p SET p.status = '2', p.startTime = :now WHERE p.status = '1' AND p.id = :projectId")
    void start(Long projectId, LocalDateTime now);
}
