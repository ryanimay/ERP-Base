package com.erp.base.model.dto.response;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Data
public class PageResponse<C> {
    private List<C> data;
    private int PageNum;
    private int pageSize;
    private int totalPage;
    private long totalElements;

    /**
     * clazz一定要有page內物件的建構子，不然會出錯
     * */
    public PageResponse(Page<?> page, Class<C> clazz) {
        List<?> content = page.getContent();
        this.data = content.stream().map(obj -> convertTo(obj, clazz)).toList();
        this.PageNum = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.totalPage = page.getTotalPages();
        this.totalElements = page.getTotalElements();
    }

    private C convertTo(Object obj, Class<C> clazz) {
        try {
            Constructor<C> constructor = clazz.getDeclaredConstructor(obj.getClass());
            return constructor.newInstance(obj);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("實例化失敗", e);
        }
    }
}
