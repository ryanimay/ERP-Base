package com.erp.base.config.quartz.job;

import com.erp.base.model.entity.SalaryModel;
import com.erp.base.model.mail.FileModel;
import com.erp.base.model.mail.SalaryMailModel;
import com.erp.base.service.MailService;
import com.erp.base.service.SalaryService;
import com.erp.base.tool.LogFactory;
import jakarta.mail.MessagingException;
import lombok.NoArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class SalaryCalculationJob implements Job {
    LogFactory LOG = new LogFactory(SalaryCalculationJob.class);
    private SalaryService salaryService;
    private MailService mailService;
    private SalaryMailModel salaryMailModel;
    private static final Map<String, String> constVal = new HashMap<>();
    static {
        constVal.put("bank", "測試銀行123ABC");
        constVal.put("account", "測試帳號ABC123");
    }
    @Autowired
    public void setSalaryMailModel(SalaryMailModel salaryMailModel) {
        this.salaryMailModel = salaryMailModel;
    }
    @Autowired
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
    @Autowired
    public void setSalaryService(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        List<SalaryModel> models = salaryService.execCalculate();
        for(SalaryModel model : models){
            String email = model.getUser().getEmail();
            org.thymeleaf.context.Context context = mailService.createContext(model.getUser().getUsername(),  model.getTime(), "$" + model.grandTotal());
            try {
                String fileName = "薪資單_" + model.getUser().getUsername() + ".xlsx";
                String classPath = "/template/excel/salary.xlsx";
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("data", model);
                dataMap.put("user", model.getUser());
                dataMap.put("const", constVal);
                //創建excel物件
                FileModel file = new FileModel(fileName, classPath, dataMap);
                mailService.sendMail(email, salaryMailModel, context, file);
            } catch (IOException e) {
                LOG.error("excel模板匯出錯誤:{0}", e.getMessage());
                throw new RuntimeException(e);
            } catch (MessagingException e) {
                LOG.error("郵件發送發生錯誤:{0}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
