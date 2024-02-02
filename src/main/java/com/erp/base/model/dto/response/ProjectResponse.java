package com.erp.base.model.dto.response;

import com.erp.base.enums.StatusConstant;
import com.erp.base.model.entity.ProjectModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectResponse {
    private long id;
    private String name;
    private String type;//1.開發案 2.維護案
    private LocalDateTime createTime;
    private ClientNameObject createBy;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;
    private String info;
    private ClientNameObject manager;
    private String status;

    public ProjectResponse(ProjectModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.type = model.getType();
        this.createTime = model.getCreateTime();
        this.createBy = new ClientNameObject(model.getCreateBy());
        this.startTime = model.getStartTime();
        this.endTime = model.getEndTime();
        this.scheduledStartTime = model.getScheduledStartTime();
        this.scheduledEndTime = model.getScheduledEndTime();
        this.info = model.getInfo();
        this.manager = new ClientNameObject(model.getManager());
        this.status = StatusConstant.get(model.getStatus());
    }
}
