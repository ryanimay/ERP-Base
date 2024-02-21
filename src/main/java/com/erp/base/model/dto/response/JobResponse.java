package com.erp.base.model.dto.response;

import com.erp.base.enums.StatusConstant;
import com.erp.base.model.entity.JobModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class JobResponse {
    private long id;
    private String info;
    private String username;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdTime;
    private String createBy;
    private String status;
    private Integer order;
    private Set<ClientNameObject> trackingSet;

    public JobResponse(JobModel model) {
        this.id = model.getId();
        this.info = model.getInfo();
        this.username = model.getUser().getUsername();
        this.startTime = model.getStartTime();
        this.endTime = model.getEndTime();
        this.createdTime = model.getCreatedTime();
        this.createBy = model.getCreateBy().getUsername();
        this.status = StatusConstant.get(model.getStatus());
        this.order = model.getOrder();
        this.trackingSet = model.getTrackingList().stream().map(ClientNameObject::new).collect(Collectors.toSet());
    }
}
