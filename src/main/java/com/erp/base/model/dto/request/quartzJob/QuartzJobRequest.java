package com.erp.base.model.dto.request.quartzJob;

import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.QuartzJobModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class QuartzJobRequest implements IBaseDto<QuartzJobModel> {
    private Long id;
    private String name;
    private String group;
    private String cron;
    private String param;
    private String info;
    private String classPath;
    private Boolean status;

    @Override
    public QuartzJobModel toModel() {
        QuartzJobModel model = new QuartzJobModel();
        model.setName(name);
        model.setGroupName(group);
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
