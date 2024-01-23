package com.erp.base.repository;

import com.erp.base.model.entity.AttendModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface AttendRepository extends JpaRepository<AttendModel, Long> {
    @Modifying
    @Query("UPDATE AttendModel a SET a.attendTime = :nowTime WHERE a.date = :nowDate AND a.user.id = :id")
    int signIn(long id, LocalDate nowDate, LocalDateTime nowTime);
    @Modifying
    @Query("UPDATE AttendModel a SET a.leaveTime = :nowTime WHERE a.date = :nowDate AND a.user.id = :id")
    int signOut(long id, LocalDate nowDate, LocalDateTime nowTime);
}
