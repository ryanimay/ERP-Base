package com.ex.erp.repository;

import com.ex.erp.model.ClientModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientModel, Long> {
    ClientModel findByUsername(String userName);
}
