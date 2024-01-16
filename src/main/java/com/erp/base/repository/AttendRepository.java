package com.erp.base.repository;

import com.erp.base.model.entity.AttendModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface AttendRepository extends JpaRepository<AttendModel, Long> {
    @Query("UPDATE AttendModel a SET a.attendTime = :nowTime WHERE a.date = :nowDate AND a.user.id = :id")
    void signIn(long id, LocalDate nowDate, LocalDateTime nowTime);

    @Query("UPDATE AttendModel a SET a.leaveTime = :nowTime WHERE a.date = :nowDate AND a.user.id = :id")
    void signOut(long id, LocalDate nowDate, LocalDateTime nowTime);
}
