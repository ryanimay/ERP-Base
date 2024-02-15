package com.erp.base.model.dto.request.quartzJob;

import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.QuartzJobModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
@Schema(description = "排程共用請求")
public class QuartzJobRequest implements IBaseDto<QuartzJobModel> {
    private static final String DEFAULT_GROUP = "DEFAULT";
    @Schema(description = "排程ID")
    private Long id;
    @Schema(description = "排程名")
    private String name;
    @Schema(description = "排程類")
    private String group;
    @Schema(description = "觸發cron")
    private String cron;
    @Schema(description = "附帶參數")
    private String param;
    @Schema(description = "描述")
    private String info;
    @Schema(description = "執行類路徑")
    private String classPath;
    @Schema(description = "狀態")
    private Boolean status;

    @Override
    public QuartzJobModel toModel() {
        QuartzJobModel model = new QuartzJobModel();
        model.setName(name);
        model.setGroupName(group == null ? DEFAULT_GROUP : group);
        model.setCron(cron);
        model.setParam(param);
        model.setClassPath(classPath);
        model.setInfo(info);
        if(status != null) model.setStatus(status);
        return model;
    }

    @Override
    public Specification<QuartzJobModel> getSpecification() {
        return null;
    }
}
