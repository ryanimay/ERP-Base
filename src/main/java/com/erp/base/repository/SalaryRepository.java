package com.erp.base.repository;

import com.erp.base.model.entity.SalaryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<SalaryModel, Long> {
    @Query("SELECT s FROM SalaryModel s WHERE s.user.id = :id ORDER BY s.time DESC")
    List<SalaryModel> findByUserId(long id);
    @Query("SELECT s FROM SalaryModel s WHERE s.root")
    List<SalaryModel> findByRoot();
    @Query("SELECT s FROM SalaryModel s WHERE s.user.id = :id AND s.root")
    Optional<SalaryModel> findByRootAndUserId(long id);
}
