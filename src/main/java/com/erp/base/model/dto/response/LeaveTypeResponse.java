package com.erp.base.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -8L;
    private int id;
    private String name;
}
