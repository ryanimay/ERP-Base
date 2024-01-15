package com.erp.base.repository;

import com.erp.base.model.entity.NotificationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationModel, Long> {
    //搜尋全域通知
    @Query("SELECT n FROM NotificationModel n WHERE n.global")
    Set<NotificationModel> findGlobal();
}
