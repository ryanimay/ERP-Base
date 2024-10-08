package com.erp.base.model.dto.request.job;

import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.JobModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Set;

@Data
@Schema(description = "任務卡共用請求")
public class JobRequest implements IBaseDto<JobModel> {
    @Schema(description = "任務卡")
    private Long id;
    @Schema(description = "任務卡內容")
    private String info;
    @Schema(description = "任務卡執行人")
    private Long userId;
    @Schema(description = "任務卡開始時間")
    private LocalDate startTime;
    @Schema(description = "任務卡結束時間")
    private LocalDate endTime;
    @Schema(description = "任務卡狀態")
    private Integer status;
    @Schema(description = "任務卡排序")
    private Integer order;
    @Schema(description = "任務卡追蹤人ID清單")
    private Set<Long> idSet;

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
    @JsonIgnore
    public Specification<JobModel> getSpecification() {
        return null;
    }
}
