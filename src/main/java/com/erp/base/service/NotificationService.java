package com.erp.base.service;

import com.erp.base.model.ClientIdentity;
import com.erp.base.model.constant.NotificationEnum;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
/**
 * 系統通知服務
 * */
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
    //保存通知
    public void save(NotificationModel model) {
        notificationRepository.save(model);
    }

    /**
     * 創建系統通知, 不指定用戶
     * @param notificationEnum 對應的enum模板
     * @param params 按順序放要替換的字元
     * @return i18n翻譯完的通知
     */
    public NotificationModel createNotification(NotificationEnum notificationEnum, Object...params){
        ClientIdentityDto user = ClientIdentity.getUser();
        NotificationModel build = NotificationModel.builder()
                .info(notificationEnum.getInfo(params))
                .router(notificationEnum.getRouterName())
                .status(false)
                .global(notificationEnum.getGlobal())
                .createBy(Objects.requireNonNull(user).getId())
                .build();
        save(build);
        return build;
    }
    /**
     * 創建系統通知, 指定用戶
     * @param notificationEnum 對應的enum模板
     * @param params 按順序放要替換的字元
     * @return i18n翻譯完的通知
     */
    public NotificationModel createNotificationToUser(NotificationEnum notificationEnum, Set<ClientModel> set, Object...params){
        ClientIdentityDto user = ClientIdentity.getUser();
        NotificationModel build = NotificationModel.builder()
                .info(notificationEnum.getInfo(params))
                .router(notificationEnum.getRouterName())
                .status(false)
                .global(notificationEnum.getGlobal())
                .createBy(Objects.requireNonNull(user).getId())
                .clients(set)
                .build();
        save(build);
        return build;
    }

    public ResponseEntity<ApiResponse> notificationStatus(IdRequest request) {
        Long id = request.getId();
        if(id == null) return ApiResponse.error(ApiResponseCode.INVALID_INPUT);
        int count = notificationRepository.updateStatusById(id);
        return count == 0 ? ApiResponse.error(ApiResponseCode.INVALID_INPUT) : ApiResponse.success(ApiResponseCode.SUCCESS);
    }
}
