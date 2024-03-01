package com.erp.base.config.quartz.job;

import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.AttendService;
import com.erp.base.service.ClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class AttendJobTest {
    @Mock
    private ClientService clientService;
    @Mock
    private JobExecutionContext jobExecutionContext;
    @Mock
    private AttendService attendService;
    @InjectMocks
    private AttendJob attendJob;

    @Test
    @DisplayName("排程任務:刷新每日打卡紀錄_成功")
    void execute() {
        Set<ClientModel> set = new HashSet<>();
        set.add(new ClientModel(1));
        set.add(new ClientModel(2));
        set.add(new ClientModel(3));
        Mockito.when(clientService.findActiveUserAndNotExistAttend()).thenReturn(set);

        attendJob.execute(jobExecutionContext);

        Mockito.verify(attendService).saveAll(Mockito.argThat(list -> {
            Assertions.assertEquals(list.get(0).getUser().getId(), 1);
            Assertions.assertNotNull(list.get(0).getDate());
            Assertions.assertEquals(list.get(1).getUser().getId(), 2);
            Assertions.assertEquals(list.get(2).getUser().getId(), 3);
            return true;
        }));
    }
}