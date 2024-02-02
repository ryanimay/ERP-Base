package com.erp.base.repository;

import com.erp.base.model.entity.SalaryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<SalaryModel, Long>, JpaSpecificationExecutor<SalaryModel> {
    @Query("SELECT s FROM SalaryModel s WHERE s.user.id = :id AND s.root = false ORDER BY s.time DESC")
    List<SalaryModel> findByUserIdAndNotRoot(long id);
    @Query("SELECT s FROM SalaryModel s WHERE s.root")
    List<SalaryModel> findByRoot();

    SalaryModel findByIdAndRootIsFalse(long id);
}
