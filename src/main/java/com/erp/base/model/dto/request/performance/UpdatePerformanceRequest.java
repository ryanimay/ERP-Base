package com.erp.base.model.dto.request.performance;

import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.PerformanceModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePerformanceRequest extends AddPerformanceRequest implements IBaseDto<PerformanceModel> {
    private Long id;
    private LocalDateTime createTime;
    @Override
    public PerformanceModel toModel() {
        PerformanceModel performanceModel = new PerformanceModel();
        performanceModel.setId(id);
        performanceModel.setEvent(this.getEvent());
        performanceModel.setFixedBonus(this.getFixedBonus());
        performanceModel.setPerformanceRatio(this.getPerformanceRatio());
        performanceModel.setCreateBy(this.getCreateBy());
        performanceModel.setCreateTime(createTime);
        performanceModel.setStatus(this.getStatus());
        return null;
    }
}