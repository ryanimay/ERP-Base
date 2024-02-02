package com.erp.base.repository;

import com.erp.base.model.entity.ClientModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface ClientRepository extends JpaRepository<ClientModel, Long> {
    ClientModel findByUsername(String userName);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    @Modifying
    @Query("UPDATE ClientModel c SET c.password = :password, c.mustUpdatePassword = :status WHERE c.username = :username AND c.email = :email")
    int updatePasswordByClient( String password, boolean status, String username, String email);
    @Modifying
    @Query("UPDATE ClientModel c SET c.isLock = :status WHERE c.id = :clientId AND c.username = :username")
    int lockClientByIdAndUsername(long clientId, String username, boolean status);
    @Modifying
    @Query("UPDATE ClientModel c SET c.isActive = :status WHERE c.id = :clientId AND c.username = :username")
    int switchClientStatusByIdAndUsername(long clientId, String username, boolean status);

    Page<ClientModel> findByIdContaining(Long id, PageRequest page);

    Page<ClientModel> findByUsernameContaining(String name, PageRequest page);
    @Query("SELECT u FROM ClientModel u WHERE u.isActive AND u.isLock = false " +
            "AND NOT EXISTS " +
            "(SELECT a FROM AttendModel a WHERE a.date = :date AND a.user = u)")
    Set<ClientModel> findActiveUserAndNotExistAttend(LocalDate date);
    @Modifying
    @Query("UPDATE ClientModel u SET u.attendStatus = 1 WHERE u.isActive AND u.isLock = false " +
            "AND EXISTS " +
            "(SELECT 1 FROM AttendModel a WHERE a.date = :date AND a.user = u)")
    void updateClientAttendStatus(LocalDate date);
    @Modifying
    @Query("UPDATE ClientModel u SET u.attendStatus = :status WHERE u.id = :id")
    void updateClientAttendStatus(long id, int status);
    //如果是主管(level1) or 管理層(level3)
    @Query("SELECT u.id FROM ClientModel u JOIN u.roles r WHERE (u.department.id = :departmentId AND r.level = :level1) OR r.level = :levelAll")
    Set<Long> queryReviewer(Long departmentId, int level1, int levelAll);
    @Query("SELECT u.id, u.username FROM ClientModel u")
    List<Object[]> findAllNameAndId();
    @Query("SELECT u.username FROM ClientModel u WHERE u.id = :id")
    String findUsernameById(long id);
    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM ClientModel c JOIN c.roles r WHERE r.id = :id) THEN true ELSE false END FROM ClientModel c")
    boolean checkExistsRoleId(Long id);
}
