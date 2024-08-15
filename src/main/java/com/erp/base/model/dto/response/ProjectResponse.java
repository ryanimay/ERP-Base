package com.erp.base.model.dto.response;

import com.erp.base.model.entity.ProjectModel;
import com.erp.base.tool.DateTool;
import lombok.Data;

@Data
public class ProjectResponse {
    private long id;
    private String name;
    private String type;//1.開發案 2.維護案 3.其他
    private String createTime;
    private ClientNameObject createBy;
    private String startTime;
    private String endTime;
    private String scheduledStartTime;
    private String scheduledEndTime;
    private String info;
    private ClientNameObject manager;
    private int status;
    private String markColor;

    public ProjectResponse(ProjectModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.type = model.getType();
        this.createTime = DateTool.format(model.getCreateTime());
        this.createBy = new ClientNameObject(model.getCreateBy());
        this.startTime = DateTool.format(model.getStartTime());
        this.endTime = DateTool.format(model.getEndTime());
        this.scheduledStartTime = DateTool.format(model.getScheduledStartTime());
        this.scheduledEndTime = DateTool.format(model.getScheduledEndTime());
        this.info = model.getInfo();
        this.manager = new ClientNameObject(model.getManager());
        this.status = model.getStatus();
        this.markColor = model.getMarkColor();
    }
}
