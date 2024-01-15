package com.erp.base.service;

import com.erp.base.model.entity.NotificationModel;
import com.erp.base.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class NotificationService {
    private NotificationRepository notificationRepository;
    @Autowired
    public void setNotificationRepository(NotificationRepository notificationRepository){
        this.notificationRepository = notificationRepository;
    }

    public Set<NotificationModel> findGlobal() {
        return notificationRepository.findGlobal();
    }
}
