package com.erp.base.model.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ClientListRequest extends PageRequestParam{
    private int type;//1.id 2.name
    private Long id;
    private String name;
}
