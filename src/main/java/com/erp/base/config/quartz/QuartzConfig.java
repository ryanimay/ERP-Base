package com.erp.base.config.quartz;

import com.erp.base.config.quartz.job.JobEnum;
import com.erp.base.tool.LogFactory;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;

@Configuration
public class QuartzConfig {
    LogFactory LOG = new LogFactory(QuartzConfig.class);
    private final ApplicationContext applicationContext;
    private final DataSource quartzDataSource;
    @Autowired
    public QuartzConfig(ApplicationContext applicationContext, DataSource quartzDataSource) {
        this.applicationContext = applicationContext;
        this.quartzDataSource = quartzDataSource;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory());
        schedulerFactoryBean.setDataSource(quartzDataSource);
        //存入所有排程任務
        Trigger[] triggers = Arrays.stream(JobEnum.values()).map(this::createTrigger).toArray(Trigger[]::new);
        schedulerFactoryBean.setTriggers(triggers);
        return schedulerFactoryBean;
    }

    @Bean
    public JobFactory jobFactory() {
        return new SpringBeanJobFactory();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public Trigger createTrigger(JobEnum jobEnum) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(jobEnum.getJobClazz());
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.afterPropertiesSet();
        if(jobDetailFactoryBean.getObject() != null){
            trigger.setJobDetail(Objects.requireNonNull(jobDetailFactoryBean.getObject()));
            trigger.setCronExpression(jobEnum.getCron());
        }
        try{
            trigger.afterPropertiesSet();
        } catch (ParseException e) {
            LOG.error("Quartz Trigger執行錯誤:{0}", e.getMessage());
        }
        return trigger.getObject();
    }
}
