package com.erp.base.config.quartz;

import com.erp.base.model.entity.QuartzJobModel;
import com.erp.base.service.QuartzJobService;
import com.erp.base.tool.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 只用來讀現有排程
 * 新增或比對的任務都在Service做
 * */
@Configuration
public class QuartzConfig {
    LogFactory LOG = new LogFactory(QuartzConfig.class);
    private final ApplicationContext applicationContext;
    private final DataSource quartzDataSource;
    private final QuartzJobService quartzJobService;
    @Autowired
    public QuartzConfig(ApplicationContext applicationContext, DataSource quartzDataSource, QuartzJobService quartzJobService) {
        this.applicationContext = applicationContext;
        this.quartzDataSource = quartzDataSource;
        this.quartzJobService = quartzJobService;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory());
        schedulerFactoryBean.setDataSource(quartzDataSource);
        try{
            schedulerFactoryBean.afterPropertiesSet();
            schedulerFactoryBean.setTriggers(getNewTrigger(schedulerFactoryBean.getScheduler()));
            LOG.info("init quartz scheduler");
        }catch (SchedulerException | ParseException e){
            LOG.error("排程檢查發生錯誤,{0}", e.getMessage());
        }catch (ClassNotFoundException e){
            LOG.error("類名轉換發生錯誤,{0}", e.getMessage());
        }catch (Exception e) {
            LOG.error("排程發生未知錯誤,{0}", e.getMessage());
        }
        return schedulerFactoryBean;
    }

    private Trigger[] getNewTrigger(Scheduler scheduler) throws SchedulerException, ClassNotFoundException, ParseException {
        List<QuartzJobModel> allModel = quartzJobService.findAll();
        List<Trigger> triggerList = new ArrayList<>();
        for (QuartzJobModel model : allModel) {
            if(!scheduler.checkExists(new TriggerKey(model.getName(), model.getGroupName()))){
                triggerList.add(quartzJobService.createTrigger(model).getObject());
            }
        }
        return triggerList.toArray(new Trigger[0]);
    }

    private JobFactory jobFactory() {
        return new SpringBeanJobFactory();
    }

    @Bean
    public Scheduler scheduler() {
        return schedulerFactoryBean().getObject();
    }
}
