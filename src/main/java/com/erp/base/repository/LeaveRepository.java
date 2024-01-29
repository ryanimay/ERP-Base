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
    int accept(long id, int status1, int status2);
    @Modifying
    @Query("UPDATE LeaveModel l SET l.status = :status2 WHERE l.id = :id AND l.status = :status1")
    int deleteByIdIfStatus(long id, int status1, int status2);
    //狀態待審並且部門相同
    @Query("SELECT l FROM LeaveModel l WHERE l.status = :status AND l.user.department.name = :departmentName")
    Page<LeaveModel> findByStatusAndDepartment(String departmentName, int status, PageRequest page);
    //狀態待審
    @Query("SELECT l FROM LeaveModel l WHERE l.status = :status")
    Page<LeaveModel> findByStatus(int status, PageRequest page);
}
