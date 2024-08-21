package com.erp.base.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Schema(description = "分頁請求")
public class PageRequestParam {
    @Schema(description = "頁數", defaultValue = "1")
    private Integer pageNum;

    @Schema(description = "每頁顯示筆數", defaultValue = "10")
    private Integer pageSize;

    @Schema(description = "正序/倒序, list傳入依照順序, 如果數量少於sortBy, 向前補正, 默認1(正序)", defaultValue = "1")
    private List<Integer> sort;

    @Schema(description = "排序屬性依據, list傳入依照順序排序", defaultValue = "id")
    private List<String> sortBy;

    public PageRequest getPage() {
        if(pageNum == null) this.pageNum = 1;
        if(pageSize == null) this.pageSize = 10;
        if(sort == null || sort.isEmpty()) this.sort = Collections.singletonList(1);
        if(sortBy == null || sortBy.isEmpty()) this.sortBy = Collections.singletonList("id");
        pageNum--;
        return PageRequest.of(pageNum, pageSize, Sort.by(handleOrders()));
    }

    /**
     * 整理page排序，依照list順序
     * */
    private List<Sort.Order> handleOrders(){
        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < sortBy.size(); i++) {
            String sortProperty = sortBy.get(i);
            int sortDirection = (i < sort.size()) ? sort.get(i) : 1; // 默認正序
            Sort.Order order = (sortDirection == 1) ? Sort.Order.asc(sortProperty) : Sort.Order.desc(sortProperty);
            orders.add(order);
        }
        return orders;
    }
}
