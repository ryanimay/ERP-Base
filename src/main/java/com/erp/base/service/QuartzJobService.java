package com.erp.base.service;

import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.request.quartzJob.QuartzJobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.QuartzJobResponse;
import com.erp.base.model.entity.QuartzJobModel;
import com.erp.base.repository.QuartzJobRepository;
import com.erp.base.tool.LogFactory;
import org.quartz.*;
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
import java.util.Optional;

@Service
@Transactional
public class QuartzJobService {
    LogFactory LOG = new LogFactory(QuartzJobService.class);
    private QuartzJobRepository quartzJobRepository;
    private Scheduler scheduler;

    @Autowired
    public void setQuartzJobRepository(QuartzJobRepository quartzJobRepository) {
        this.quartzJobRepository = quartzJobRepository;
    }

    @Autowired
    public void setScheduler(@Lazy Scheduler scheduler) {
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
        try {
            QuartzJobModel model = request.toModel();
            CronTriggerFactoryBean triggerBean = createTrigger(model);
            JobDetail jobDetail = (JobDetail) triggerBean.getJobDataMap().get("jobDetail");
            if (scheduler.checkExists(jobDetail.getKey())) {
                throw new ObjectAlreadyExistsException("");
            }
            CronTrigger trigger = triggerBean.getObject();
            AddQuartzJob(jobDetail, trigger);
            //先存schedule確認沒有錯誤再插入表
            quartzJobRepository.save(model);
            //如果設置狀態為false就先暫停
            if (!model.isStatus()) {
                scheduler.pauseJob(Objects.requireNonNull(trigger).getJobKey());
            }
        } catch (ClassNotFoundException e) {
            response = ApiResponse.errorMsgFormat(ApiResponseCode.CLASS_NOT_FOUND, request.getClassPath());
        } catch (ObjectAlreadyExistsException e) {
            response = ApiResponse.errorMsgFormat(ApiResponseCode.JOB_NAME_EXISTS, request.getName());
        } catch (ParseException e) {
            response = ApiResponse.errorMsgFormat(ApiResponseCode.CRON_ERROR, request.getCron());
        } catch (SchedulerException e) {
            response = ApiResponse.errorMsgFormat(ApiResponseCode.SCHEDULER_ERROR, e.getMessage());
        }
        return response;
    }

    /**
     * 把新增的任務加到現有排程器內
     */
    private void AddQuartzJob(JobDetail jobDetail, CronTrigger trigger) throws SchedulerException {
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public ResponseEntity<ApiResponse> update(QuartzJobRequest request) throws ClassNotFoundException, SchedulerException, ParseException {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        Long id = request.getId();
        Optional<QuartzJobModel> byId = quartzJobRepository.findById(id);
        if (byId.isPresent()) {
            QuartzJobModel model = byId.get();
            //有關triggerKey生成，不能更動
//            if (request.getName() != null) model.setName(request.getName());
//            if (request.getGroup() != null) model.setGroupName(request.getGroup());
            if (request.getCron() != null) model.setCron(request.getCron());
            if (request.getParam() != null) model.setParam(request.getParam());
            if (request.getInfo() != null) model.setInfo(request.getInfo());
            if (request.getClassPath() != null) model.setClassPath(request.getClassPath());
            quartzJobRepository.save(model);

            CronTrigger trigger = createTrigger(model).getObject();
            if (trigger == null) throw new SchedulerException();
            //更新現有排程器內的任務內容
            scheduler.rescheduleJob(trigger.getKey(), trigger);
            updateQuartzTableData(model);
        } else {
            response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "JobId Not Found.");
        }
        return response;
    }

    public void delete(Long id) throws SchedulerException {
        Optional<QuartzJobModel> byId = quartzJobRepository.findById(id);
        if (byId.isPresent()) {
            QuartzJobModel model = byId.get();
            JobKey jobKey = new JobKey(model.getName(), model.getGroupName());
            scheduler.deleteJob(jobKey);//清除伺服器當前排程器內任務
            deleteFromQuartzTableByName(model.getName());
            quartzJobRepository.deleteById(id);
        }
    }

    //更新預設表中的資料
    private void updateQuartzTableData(QuartzJobModel model) {
        String name = model.getName();
        String cron = model.getCron();
        String classPath = model.getClassPath();
        quartzJobRepository.updateFromJobDetails(classPath, name);
        quartzJobRepository.updateFromCronTriggers(cron, name);
    }

    //清除預設表中的資料，避免重啟專案後又讀到
    private void deleteFromQuartzTableByName(String name) {
        quartzJobRepository.deleteFromTriggersByName(name);
        quartzJobRepository.deleteFromJobDetailsByName(name);
        quartzJobRepository.deleteFromCronTriggersByName(name);
    }

    @SuppressWarnings("unchecked")
    public CronTriggerFactoryBean createTrigger(QuartzJobModel model) throws ClassNotFoundException, ParseException {
        Class<? extends Job> jobClass;
        jobClass = (Class<? extends Job>) Class.forName(model.getClassPath());
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(jobClass);
        jobDetailFactoryBean.setName(model.getName());
        jobDetailFactoryBean.setGroup(model.getGroupName());
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.afterPropertiesSet();
        if (jobDetailFactoryBean.getObject() != null) {
            trigger.setJobDetail(Objects.requireNonNull(jobDetailFactoryBean.getObject()));
            trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);//錯過觸發點不補執行
            trigger.setCronExpression(model.getCron());
            trigger.setName(model.getName());
            trigger.setGroup(model.getGroupName());
        }
        try {
            trigger.afterPropertiesSet();
        } catch (ParseException e) {
            LOG.error("Quartz Trigger執行錯誤:{0}", e.getMessage());
            throw e;
        }
        return trigger;
    }

    public ResponseEntity<ApiResponse> toggle(IdRequest request) throws SchedulerException {
        Long id = request.getId();
        quartzJobRepository.switchStatusById(id);
        Optional<QuartzJobModel> byId = quartzJobRepository.findById(id);
        if (byId.isPresent()) {
            QuartzJobModel model = byId.get();
            boolean status = model.isStatus();
            JobKey jobKey = new JobKey(model.getName(), model.getGroupName());
            if (status) {
                scheduler.resumeJob(jobKey);
            } else {
                scheduler.pauseJob(jobKey);
            }
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }else{
            return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "JobId Not Found.");
        }
    }

    public void exec(IdRequest request) throws SchedulerException {
        Long id = request.getId();
        Optional<QuartzJobModel> byId = quartzJobRepository.findById(id);
        if (byId.isPresent()) {
            QuartzJobModel model = byId.get();
            scheduler.triggerJob(new JobKey(model.getName(), model.getGroupName()));
        }
    }
}
