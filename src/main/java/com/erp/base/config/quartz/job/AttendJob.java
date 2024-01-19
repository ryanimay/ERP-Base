package com.erp.base.config.quartz.job;

import com.erp.base.model.entity.AttendModel;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.AttendService;
import com.erp.base.service.ClientService;
import lombok.NoArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
/**
 * 刷新每日打卡紀錄
 * */
@Component
@NoArgsConstructor
public class AttendJob implements Job {
    private ClientService clientService;
    private AttendService attendService;

    @Autowired
    public void setAttendService(AttendService attendService){
        this.attendService = attendService;
    }
    @Autowired
    public void setClientService(ClientService clientService){
        this.clientService = clientService;
    }
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        Set<ClientModel> activeUser = clientService.findActiveUserAndNotExistAttend();
        List<AttendModel> attends = activeUser.stream().map(AttendModel::new).toList();
        attendService.saveAll(attends);
    }
}
