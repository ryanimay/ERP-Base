package com.erp.base.model.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class AnnualLeaveDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 4L;
    private String currentLeave;
    private String totalLeave;
    private String pendingLeave;

    public AnnualLeaveDto(String currentLeave, String totalLeave, Long pendingLeave) {
        this.currentLeave = currentLeave;
        this.totalLeave = totalLeave;
        this.pendingLeave = pendingLeave == null ? "0" : String.valueOf(pendingLeave);
    }
    /**
     * IDE版本問題，JPQL對應會顯示constructor錯誤，所以加上Integer為參數的constructor，實際建構是用Long
     * */
    public AnnualLeaveDto(String currentLeave, String totalLeave, Integer pendingLeave) {
        this.currentLeave = currentLeave;
        this.totalLeave = totalLeave;
        this.pendingLeave = pendingLeave == null ? "0" : String.valueOf(pendingLeave);
    }
}
