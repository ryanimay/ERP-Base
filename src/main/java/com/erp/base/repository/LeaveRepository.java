package com.erp.base.repository;

import com.erp.base.model.entity.LeaveModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveModel, Long> {
    @Query("SELECT l FROM LeaveModel l WHERE l.user.id = :id")
    Page<LeaveModel> findAllByUser(long id, PageRequest page);
    @Modifying
    @Query("UPDATE LeaveModel l SET l.status = :status2 WHERE l.id = :id AND l.status = :status1")
    int accept(long id, String status1, String status2);
    @Modifying
    @Query("UPDATE LeaveModel l SET l.status = :status2 WHERE l.id = :id AND l.status = :status1")
    int deleteByIdIfStatus(long id, String status1, String status2);
    @Query("SELECT l FROM LeaveModel l WHERE l.status = :status")
    Page<LeaveModel> findAllByStatus(String status, PageRequest page);
}
