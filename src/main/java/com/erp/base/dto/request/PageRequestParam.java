package com.erp.base.dto.request;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
public class PageRequestParam {
    private int pageNum = 1;
    private int pageSize = 15;
    private int sort = 1;
    private String sortBy = "id";

    public PageRequest getPage() {
        pageNum--;
        if (sort == 1) return PageRequest.of(pageNum, pageSize, Sort.by(sortBy).ascending());
        else return PageRequest.of(pageNum, pageSize, Sort.by(sortBy).descending());
    }
}
