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
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class SalaryService {
    private SalaryRepository salaryRepository;
    private MessageService messageService;
    private NotificationService notificationService;
    private ClientService clientService;

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
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
        Long userId = request.getUserId();
        String userName = Optional.ofNullable(userId)
                .map(clientService::findNameByUserId)
                .orElse(null);
        if(userName == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        //找該用戶的薪資設定是否存在，存在就編輯，不存在就新增
        SalaryModel userSalaryModel = Optional.ofNullable(salaryRepository.findByUserIdAndRoot(userId, true))
                .orElseGet(request::toModel);
        Optional.ofNullable(request.getBaseSalary()).ifPresent(userSalaryModel::setBaseSalary);
        Optional.ofNullable(request.getMealAllowance()).ifPresent(userSalaryModel::setMealAllowance);
        Optional.ofNullable(request.getBonus()).ifPresent(userSalaryModel::setBonus);
        Optional.ofNullable(request.getLaborInsurance()).ifPresent(userSalaryModel::setLaborInsurance);
        Optional.ofNullable(request.getNationalHealthInsurance()).ifPresent(userSalaryModel::setNationalHealthInsurance);

        userSalaryModel.setRoot(true);
        salaryRepository.save(userSalaryModel);
        sendMessage(userId);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    private void sendMessage(Long userId) {
        ClientModel user = ClientIdentity.getUser();
        NotificationModel notification = notificationService.createNotification(NotificationEnum.EDIT_SALARY_ROOT);
        MessageModel messageModel = new MessageModel(Objects.requireNonNull(user).getUsername(), Long.toString(userId), WebsocketConstant.TOPIC.NOTIFICATION, notification);
        messageService.sendTo(messageModel);
    }

    public ResponseEntity<ApiResponse> get() {
        ClientModel user = ClientIdentity.getUser();
        if (user == null) {
            return ApiResponse.success(ApiResponseCode.USER_NOT_FOUND);
        }
        List<SalaryModel> salaryList = salaryRepository.findByUserIdAndNotRoot(user.getId());
        List<SalaryResponse> salaryResponses = salaryList.stream().map(SalaryResponse::new).toList();
        return ApiResponse.success(ApiResponseCode.SUCCESS, salaryResponses);
    }

    public ResponseEntity<ApiResponse> info(Long id) {
        SalaryModel salaryModel = Optional.ofNullable(id).map(salaryRepository::findByIdAndRootIsFalse).orElse(null);
        if(salaryModel == null) return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id[" + id + "] Not Found");
        return ApiResponse.success(ApiResponseCode.SUCCESS, new SalaryResponse(salaryModel));
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
