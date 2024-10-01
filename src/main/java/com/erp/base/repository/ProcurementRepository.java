package com.erp.base.repository;

import com.erp.base.model.entity.ProcurementModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcurementRepository extends JpaRepository<ProcurementModel, Long>, JpaSpecificationExecutor<ProcurementModel> {
    @Query("SELECT " +
            " COALESCE(SUM(CASE WHEN p.type = 1 THEN p.price ELSE 0 END), 0)," +
            " COALESCE(SUM(CASE WHEN p.type = 2 THEN p.price ELSE 0 END), 0) " +
            " FROM ProcurementModel p " +
            " WHERE YEAR(p.createTime) = YEAR(CURRENT_DATE) " +
            " AND MONTH(p.createTime) = MONTH(CURRENT_DATE)")
    List<Object[]> getSystemProcure();
}
