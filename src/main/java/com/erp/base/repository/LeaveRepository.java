package com.erp.base.repository;

import com.erp.base.model.entity.LeaveModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveModel, Long>, JpaSpecificationExecutor<LeaveModel> {
    @Modifying
    @Query("UPDATE LeaveModel l SET l.status = :status2 WHERE l.id = :id AND l.status = :status1")
    int updateLeaveStatus(long id, int status1, int status2);
    //狀態待審並且部門相同
    @Query("SELECT l FROM LeaveModel l WHERE l.status = :status AND l.user.department.id = :id AND l.user.id <> :userId")
    Page<LeaveModel> findByStatusAndDepartment(long userId, long id, int status, PageRequest page);
    //狀態待審
    @Query("SELECT l FROM LeaveModel l WHERE l.status = :status AND l.user.id <> :userId")
    Page<LeaveModel> findByStatus(long userId, int status, PageRequest page);
    @Modifying
    @Query("DELETE FROM LeaveModel l  WHERE l.id = :id AND l.status = :pendingNo")
    int deleteByIdAndStatus(long id, int pendingNo);
}
