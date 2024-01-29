package com.erp.base.repository;

import com.erp.base.model.entity.DepartmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentModel, Long> {
}
