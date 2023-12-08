package com.erp.base.dto.request;

import com.erp.base.model.IBaseModel;

public interface IBaseDto<C extends IBaseModel> {
    C toModel();
}
