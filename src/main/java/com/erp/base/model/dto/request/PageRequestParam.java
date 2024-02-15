package com.erp.base.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
@Schema(description = "分頁請求")
public class PageRequestParam {
    @Schema(description = "頁數")
    private int pageNum = 1;
    @Schema(description = "每頁顯示筆數")
    private int pageSize = 15;
    @Schema(description = "正序/倒序")
    private int sort = 1;
    @Schema(description = "排序屬性依據")
    private String sortBy = "id";

    public PageRequest getPage() {
        pageNum--;
        if (sort == 1) return PageRequest.of(pageNum, pageSize, Sort.by(sortBy).ascending());
        else return PageRequest.of(pageNum, pageSize, Sort.by(sortBy).descending());
    }
}
