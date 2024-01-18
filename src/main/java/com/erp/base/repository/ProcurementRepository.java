package com.erp.base.repository;

import com.erp.base.model.entity.ProcurementModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcurementRepository extends JpaRepository<ProcurementModel, Long> {
}
