package com.erp.base.repository;

import com.erp.base.model.entity.JobModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobModel, Long> {
}
