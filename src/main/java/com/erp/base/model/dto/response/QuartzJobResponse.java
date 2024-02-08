package com.erp.base.model.dto.response;

import com.erp.base.model.entity.QuartzJobModel;
import lombok.Data;

@Data
public class QuartzJobResponse {
    private long id;
    private String name;
    private String group;
    private String cron;
    private String param;
    private String info;
    private String classPath;
    private boolean status;

    public QuartzJobResponse(QuartzJobModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.group = model.getGroupName();
        this.cron = model.getCron();
        this.param = model.getParam();
        this.info = model.getInfo();
        this.classPath = model.getClassPath();
        this.status = model.isStatus();
    }
}
