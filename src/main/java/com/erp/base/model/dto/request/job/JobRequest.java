package com.erp.base.model.dto.request.job;

import com.erp.base.enums.StatusConstant;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.JobModel;
import com.erp.base.tool.DateTool;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class JobRequest implements IBaseDto<JobModel> {
    private Long id;
    private String info;
    private Long userId;
    @DateTimeFormat(pattern = DateTool.YYYY_MM_DD_T_HH_MM_SS)
    private LocalDateTime startTime;
    @DateTimeFormat(pattern = DateTool.YYYY_MM_DD_T_HH_MM_SS)
    private LocalDateTime endTime;
    private int status;
    private Integer order;
    private Set<Integer> idSet;

    @Override
    public JobModel toModel() {
        JobModel job = new JobModel();
        if(id != null) job.setId(id);
        job.setInfo(info);
        if(userId != null) job.setUser(new ClientModel(userId));
        job.setStartTime(startTime);
        job.setEndTime(endTime);
        job.setOrder(order);
        if(idSet != null){
            idSet.forEach(id -> job.addTracking(new ClientModel(id)));
        }
        return job;
    }

    @Override
    public Specification<JobModel> getSpecification() {
        return null;
    }

    public String getStatus() {
        return StatusConstant.get(status);
    }
}
