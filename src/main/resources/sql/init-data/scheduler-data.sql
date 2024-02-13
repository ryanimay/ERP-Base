--預設排程任務
INSERT INTO quartz_job (class_path, cron, group_name, info, name, param, status)
SELECT class_path, cron, group_name, info, name, param, status
FROM (VALUES ('com.erp.base.config.quartz.job.TestJob', '*/3 * * * * ?', 'DEFAULT', '測試任務說明', '測試任務', null, 1),
             ('com.erp.base.config.quartz.job.AttendJob', '0 0 0 * * ?', 'DEFAULT', '刷新每日簽到紀錄', '刷新簽到任務', null, 1),
             ('com.erp.base.config.quartz.job.SalaryCalculationJob', '0 0 10 30 * ?', 'DEFAULT', '月結薪資', '月結薪資任務', null, 1)) AS source(class_path, cron, group_name, info, name, param, status)
WHERE NOT EXISTS(SELECT 1 FROM quartz_job WHERE source.class_path = quartz_job.class_path);
