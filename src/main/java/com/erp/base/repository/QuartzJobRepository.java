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
}
