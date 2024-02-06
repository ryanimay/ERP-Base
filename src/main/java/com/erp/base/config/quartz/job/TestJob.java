package com.erp.base.config.quartz.job;

import com.erp.base.tool.LogFactory;
import lombok.NoArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class TestJob implements Job {
    LogFactory LOG = new LogFactory(TestJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        LOG.warn("test success!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
}
