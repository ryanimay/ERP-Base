package com.erp.base.service;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.request.quartzJob.QuartzJobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.QuartzJobResponse;
import com.erp.base.model.entity.QuartzJobModel;
import com.erp.base.repository.QuartzJobRepository;
import com.erp.base.tool.LogFactory;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class QuartzJobService {
    LogFactory LOG = new LogFactory(QuartzJobService.class);
    private QuartzJobRepository quartzJobRepository;
    private Scheduler scheduler;
    @Autowired
    public void setQuartzJobRepository(QuartzJobRepository quartzJobRepository){
        this.quartzJobRepository = quartzJobRepository;
    }
    @Autowired
    public void setScheduler(@Lazy Scheduler scheduler){
        this.scheduler = scheduler;
    }

    public ResponseEntity<ApiResponse> list() {
        List<QuartzJobModel> all = findAll();
        List<QuartzJobResponse> quartzJobResponses = all.stream().map(QuartzJobResponse::new).toList();
        return ApiResponse.success(ApiResponseCode.SUCCESS, quartzJobResponses);
    }

    public List<QuartzJobModel> findAll() {
        return quartzJobRepository.findAll();
    }

    public ResponseEntity<ApiResponse> add(QuartzJobRequest request) {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        QuartzJobModel model = request.toModel();
        quartzJobRepository.save(model);
        CronTriggerFactoryBean trigger;
        try{
            trigger = createTrigger(model);
            AddQuartzJob(trigger);
        }catch (ClassNotFoundException e){
            response = ApiResponse.error(ApiResponseCode.CLASS_NOT_FOUND);
        }catch (SchedulerException e){
            response = ApiResponse.error(ApiResponseCode.SCHEDULER_ERROR);
        }
        return response;
    }

    private void AddQuartzJob(CronTriggerFactoryBean trigger) throws SchedulerException {
        scheduler.scheduleJob((JobDetail)trigger.getJobDataMap().get("jobDetail"), trigger.getObject());
    }

    public ResponseEntity<ApiResponse> update() {
        return null;
    }

    public ResponseEntity<ApiResponse> delete() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public CronTriggerFactoryBean createTrigger(QuartzJobModel model) throws ClassNotFoundException {
        Class<? extends Job> jobClass;
        jobClass = (Class<? extends Job>) Class.forName(model.getClassPath());
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(jobClass);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.afterPropertiesSet();
        if(jobDetailFactoryBean.getObject() != null){
            trigger.setJobDetail(Objects.requireNonNull(jobDetailFactoryBean.getObject()));
            trigger.setCronExpression(model.getCron());
            trigger.setName(model.getName());
            trigger.setGroup(model.getGroup());
        }
        try{
            trigger.afterPropertiesSet();
        } catch (ParseException e) {
            LOG.error("Quartz Trigger執行錯誤:{0}", e.getMessage());
        }
        return trigger;
    }
}
