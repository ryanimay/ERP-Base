package com.erp.base.config.quartz.job;

import org.quartz.Job;

public enum JobEnum {
    SALARY(SalaryCalculationJob.class, "0 0 10 30 * ?"),
    ATTEND(AttendJob.class, "0 0 0 * * ?")
    ;
    private final Class<? extends Job> jobClazz;
    private final String cron;

    JobEnum(Class<? extends Job> jobClazz, String cron) {
        this.jobClazz = jobClazz;
        this.cron = cron;
    }

    public Class<? extends Job> getJobClazz() {
        return jobClazz;
    }

    public String getCron() {
        return cron;
    }
}
