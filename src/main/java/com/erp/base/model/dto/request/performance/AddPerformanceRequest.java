package com.erp.base.model.dto.request.performance;

import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.PerformanceModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPerformanceRequest implements IBaseDto<PerformanceModel> {
    private String event;
    private BigDecimal fixedBonus;
    private BigDecimal performanceRatio;
    private LocalDateTime eventTime;
    private Long createBy;
    private Long status = 1L;
    @Override
    public PerformanceModel toModel() {
        PerformanceModel performanceModel = new PerformanceModel();
        performanceModel.setEvent(event);
        performanceModel.setFixedBonus(fixedBonus);
        performanceModel.setPerformanceRatio(performanceRatio);
        performanceModel.setCreateBy(createBy);
        performanceModel.setStatus(status);
        return null;
    }

    @Override
    public Specification<PerformanceModel> getSpecification() {
        return null;
    }
}
