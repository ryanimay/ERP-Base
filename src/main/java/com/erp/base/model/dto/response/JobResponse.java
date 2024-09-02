package com.erp.base.model.dto.response;

import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.entity.JobModel;
import com.erp.base.tool.DateTool;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class JobResponse {
    private long id;
    private String info;
    private ClientNameObject user;
    private String startTime;
    private String endTime;
    private String createdTime;
    private String createBy;
    private String status;
    private Integer order;
    private Set<ClientNameObject> trackingSet;

    public JobResponse(JobModel model) {
        this.id = model.getId();
        this.info = model.getInfo();
        this.user = new ClientNameObject(model.getUser());
        this.startTime = DateTool.formatDate(model.getStartTime());
        this.endTime = DateTool.formatDate(model.getEndTime());
        this.createdTime = DateTool.format(model.getCreatedTime());
        this.createBy = model.getCreateBy().getUsername();
        this.status = StatusConstant.get(model.getStatus());
        this.order = model.getOrder();
        this.trackingSet = model.getTrackingList().stream().map(ClientNameObject::new).collect(Collectors.toSet());
    }
}
