package com.erp.base.model.dto.request;

import com.erp.base.model.entity.IBaseModel;

public interface IBaseDto<C extends IBaseModel> {
    C toModel();
}
