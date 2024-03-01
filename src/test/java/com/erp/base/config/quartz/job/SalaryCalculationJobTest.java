package com.erp.base.config.quartz.job;

import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.SalaryModel;
import com.erp.base.model.mail.SalaryMailModel;
import com.erp.base.service.MailService;
import com.erp.base.service.SalaryService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SalaryCalculationJobTest {
    @Mock
    private SalaryService salaryService;
    @Mock
    private MailService mailService;
    @Mock
    private SalaryMailModel salaryMailModel;
    @Mock
    private JobExecutionContext jobExecutionContext;
    @InjectMocks
    private SalaryCalculationJob SalaryCalculationJob;

    @Test
    @DisplayName("排程任務:月結薪資單_成功")
    void execute() throws MessagingException {
        List<SalaryModel> list = new ArrayList<>();
        list.add(createSalary(1, "user1", 50000));
        list.add(createSalary(2, "user2", 40000));
        list.add(createSalary(3, "user3", 30000));
        Mockito.when(salaryService.execCalculate()).thenReturn(list);
        SalaryCalculationJob.execute(jobExecutionContext);
        Mockito.verify(mailService, Mockito.times(3)).sendMail(Mockito.any(), Mockito.eq(salaryMailModel), Mockito.any(), Mockito.any());
    }

    private SalaryModel createSalary(int uid, String userName, int base){
        SalaryModel salaryModel = new SalaryModel();
        ClientModel user = new ClientModel(uid);
        user.setUsername(userName);
        salaryModel.setUser(user);
        salaryModel.setBaseSalary(new BigDecimal(base));
        return salaryModel;
    }
}