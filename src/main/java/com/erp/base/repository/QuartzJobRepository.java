package com.erp.base.repository;

import com.erp.base.model.entity.QuartzJobModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuartzJobRepository extends JpaRepository<QuartzJobModel, Long> {
}
