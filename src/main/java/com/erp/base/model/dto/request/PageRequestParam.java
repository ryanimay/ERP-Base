package com.erp.base.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
@Schema(description = "分頁請求")
public class PageRequestParam {
    @Schema(description = "頁數", defaultValue = "1")
    private Integer pageNum;

    @Schema(description = "每頁顯示筆數", defaultValue = "10")
    private Integer pageSize;

    @Schema(description = "正序/倒序", defaultValue = "1")
    private Integer sort;

    @Schema(description = "排序屬性依據", defaultValue = "id")
    private String sortBy;

    public PageRequest getPage() {
        if(pageNum == null) this.pageNum = 1;
        if(pageSize == null) this.pageSize = 10;
        if(sort == null) this.sort = 1;
        if(sortBy == null) this.sortBy = "id";
        pageNum--;
        if (sort == 1) return PageRequest.of(pageNum, pageSize, Sort.by(sortBy).ascending());
        else return PageRequest.of(pageNum, pageSize, Sort.by(sortBy).descending());
    }
}
