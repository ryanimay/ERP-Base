package com.ex.erp.repository;

import com.ex.erp.dto.request.ResetPasswordRequest;
import com.ex.erp.model.ClientModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientModel, Long> {
    ClientModel findByUsername(String userName);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    @Modifying
    @Query("UPDATE ClientModel c SET c.password = :password WHERE c.username = :#{#request.username} AND  c.email = :#{#request.email}")
    int updatePasswordByClient(@Param("password") String password, @Param("request") ResetPasswordRequest resetRequest);
}
