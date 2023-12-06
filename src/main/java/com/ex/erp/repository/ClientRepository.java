package com.ex.erp.repository;

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
    @Query("UPDATE ClientModel c SET c.password = :password WHERE c.username = :username AND  c.email = :email")
    int updatePasswordByClient(@Param("password") String password, @Param("username") String username, @Param("email") String email);
    @Query("UPDATE ClientModel c SET c.isLock = :lock WHERE c.id = :id")
    void lockClientById(@Param("id") long clientId, @Param("lock") boolean status);
}
