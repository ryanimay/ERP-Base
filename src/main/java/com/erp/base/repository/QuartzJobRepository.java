package com.erp.base.repository;

import com.erp.base.model.entity.QuartzJobModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuartzJobRepository extends JpaRepository<QuartzJobModel, Long> {
    @Modifying
    @Query("UPDATE QuartzJobModel q SET q.status = CASE WHEN q.status = true THEN false ELSE true END WHERE q.id = :id")
    void switchStatusById(Long id);
    @Modifying
    @Query(value = "DELETE FROM QRTZ_JOB_DETAILS WHERE JOB_NAME = :name",nativeQuery = true)
    void deleteFromJobDetailsByName(String name);
    @Modifying
    @Query(value = "DELETE FROM QRTZ_TRIGGERS WHERE TRIGGER_NAME = :name",nativeQuery = true)
    void deleteFromTriggersByName(String name);
    @Modifying
    @Query(value = "DELETE FROM QRTZ_CRON_TRIGGERS WHERE TRIGGER_NAME = :name",nativeQuery = true)
    void deleteFromCronTriggersByName(String name);
    @Modifying
    @Query(value = "UPDATE QRTZ_JOB_DETAILS SET JOB_CLASS_NAME = :classPath WHERE JOB_NAME = :name",nativeQuery = true)
    void updateFromJobDetails(String classPath, String name);
    @Modifying
    @Query(value = "UPDATE QRTZ_CRON_TRIGGERS SET CRON_EXPRESSION = :cron WHERE TRIGGER_NAME = :name",nativeQuery = true)
    void updateFromCronTriggers(String cron, String name);
}
