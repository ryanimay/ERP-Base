package com.erp.base.service;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.enums.NotificationEnum;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.MessageModel;
import com.erp.base.model.dto.request.salary.SalaryRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.dto.response.SalaryResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.model.entity.SalaryModel;
import com.erp.base.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SalaryService {
    private SalaryRepository salaryRepository;
    private MessageService messageService;
    private NotificationService notificationService;

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setSalaryRepository(SalaryRepository salaryRepository) {
        this.salaryRepository = salaryRepository;
    }

    public ResponseEntity<ApiResponse> getRoots(SalaryRequest request) {
        Page<SalaryModel> roots = salaryRepository.findAll(request.getSpecification(), request.getPage());
        return ApiResponse.success(new PageResponse<>(roots, SalaryResponse.class));
    }

    public ResponseEntity<ApiResponse> editRoot(SalaryRequest request) {
        SalaryModel salaryModel = request.toModel();
        salaryRepository.save(salaryModel);
        sendMessage(request.getUserId());
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    private void sendMessage(Long userId) {
        ClientModel user = ClientIdentity.getUser();
        NotificationModel notification = notificationService.createNotification(NotificationEnum.EDIT_SALARY_ROOT);
        MessageModel messageModel = new MessageModel(user.getUsername(), Long.toString(userId), WebsocketConstant.TOPIC.NOTIFICATION, notification);
        messageService.sendTo(messageModel);
    }

    public ResponseEntity<ApiResponse> get() {
        ClientModel user = ClientIdentity.getUser();
        if (user == null) {
            return ApiResponse.success(ApiResponseCode.USER_NOT_FOUND);
        }
        List<SalaryModel> salaryList = salaryRepository.findByUserId(user.getId());
        return ApiResponse.success(ApiResponseCode.SUCCESS, salaryList);
    }

    public ResponseEntity<ApiResponse> info(long id) {
        Optional<SalaryModel> salaryModel = salaryRepository.findById(id);
        return ApiResponse.success(ApiResponseCode.SUCCESS, salaryModel.orElse(null));
    }

    //執行統計彙整的動作
    public List<SalaryModel> execCalculate() {
        LocalDate now = LocalDate.now();
        List<SalaryModel> roots = salaryRepository.findByRoot();
        List<SalaryModel> newList = new ArrayList<>();
        for (SalaryModel root : roots) {
            root.setId(null);
            root.setTime(now);
            root.setRoot(false);
            newList.add(root);
        }
        return salaryRepository.saveAll(newList);
    }
}
