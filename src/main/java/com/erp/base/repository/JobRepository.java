package com.erp.base.repository;

import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.JobModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobModel, Long> {
    @Query("SELECT j FROM JobModel j WHERE j.user = :model OR :model MEMBER OF j.trackingList ORDER BY j.order NULLS LAST")
    List<JobModel> findByUserId(@Param("model") ClientModel model);
}
