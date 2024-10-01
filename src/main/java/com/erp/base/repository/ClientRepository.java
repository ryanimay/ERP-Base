package com.erp.base.repository;

import com.erp.base.model.dto.response.AnnualLeaveDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.NotificationModel;
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

    boolean existsByUsernameAndEmail(String username, String email);
    @Modifying
    @Query("UPDATE ClientModel c " +
            "SET c.password = :password, c.mustUpdatePassword = :status " +
            "WHERE c.username = :username " +
            "AND (:id IS NULL OR c.id = :id) "+
            "AND (:email IS NULL OR c.email = :email)")
    int updatePasswordByUsernameAndEmailAndId(String password, boolean status, String username, String email,Long id);
    @Modifying
    @Query("UPDATE ClientModel c SET c.isLock = :status WHERE c.id = :clientId AND c.username = :username")
    int lockClientByIdAndUsername(long clientId, String username, boolean status);
    @Modifying
    @Query("UPDATE ClientModel c SET c.isActive = :status WHERE c.id = :clientId AND c.username = :username")
    int switchClientStatusByIdAndUsername(long clientId, String username, boolean status);
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
    int updateClientAttendStatus(long id, int status);
    //如果是主管(level1) or 管理層(level3)
    @Query("SELECT u FROM ClientModel u JOIN u.roles r WHERE (u.department.id = :departmentId AND r.level = :level1) OR r.level = :levelAll")
    Set<ClientModel> queryReviewer(Long departmentId, int level1, int levelAll);
    @Query("SELECT u.id, u.username FROM ClientModel u")
    List<Object[]> findAllNameAndId();
    @Query("SELECT u.username FROM ClientModel u WHERE u.id = :id")
    String findUsernameById(long id);
    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM ClientModel c JOIN c.roles r WHERE r.id = :id) THEN true ELSE false END FROM ClientModel c")
    boolean checkExistsRoleId(Long id);
    Page<ClientModel> findById(Long id, PageRequest page);
    @Query("SELECT n FROM NotificationModel n JOIN n.clients c WHERE c.id = :id")
    Set<NotificationModel> findNotificationByUserId(Long id);
    @Query("SELECT new com.erp.base.model.dto.response.AnnualLeaveDto(" +
            "COALESCE(c.annualLeave.currentLeave, 0), " +
            "COALESCE(c.annualLeave.totalLeave, 0), " +
            "( " +
            "   SELECT COUNT(l) FROM LeaveModel l WHERE c.id = l.user.id AND l.status = 1" +
            ")) " +
            " FROM ClientModel c WHERE c.id = :id ")
    AnnualLeaveDto getClientLeave(long id);
    @Query("SELECT COALESCE(SUM(CASE WHEN c.attendStatus = 2 THEN 1 ELSE 0 END), 0) AS sum, COUNT(c) AS num FROM ClientModel c WHERE c.isLock <> true AND c.isActive <> false")
    List<Object[]> getSystemUser();
}
