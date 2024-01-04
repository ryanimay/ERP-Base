package com.erp.base.repository;

import com.erp.base.model.RouterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouterRepository extends JpaRepository<RouterModel, Long> {
}
