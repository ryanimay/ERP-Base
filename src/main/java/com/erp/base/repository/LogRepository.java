package com.erp.base.repository;

import com.erp.base.model.entity.LogModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<LogModel, Long>, JpaSpecificationExecutor<LogModel> {
}
