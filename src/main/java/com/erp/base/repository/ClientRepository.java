package com.erp.base.repository;

import com.erp.base.model.ClientModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientModel, Long> {
    ClientModel findByUsername(String userName);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    @Modifying
    @Query("UPDATE ClientModel c SET c.password = :password WHERE c.username = :username AND c.email = :email")
    int updatePasswordByClient( String password, String username, String email);
    @Modifying
    @Query("UPDATE ClientModel c SET c.isLock = :status WHERE c.id = :clientId AND c.username = :username")
    void lockClientByIdAndUsername(long clientId, String username, boolean status);
    @Modifying
    @Query("UPDATE ClientModel c SET c.isActive = :status WHERE c.id = :clientId AND c.username = :username")
    void switchClientStatusByIdAndUsername(long clientId, String username, boolean status);
}
