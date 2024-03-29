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
     * 把Page<Entity>替換成Page<DTO>
     * clazz一定要有page內物件的建構子，不然會出錯
     * ex:DTO一定要有entity為參數的建構子
     * */
    public PageResponse(Page<?> page, Class<C> clazz) {
        List<?> content = page.getContent();
        this.data = content.stream().map(obj -> convertTo(obj, clazz)).toList();
        this.PageNum = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.totalPage = page.getTotalPages();
        this.totalElements = page.getTotalElements();
    }
    @SuppressWarnings("unchecked")
    private C convertTo(Object obj, Class<C> clazz) {
        if (clazz.isInstance(obj)) return (C)obj;
        try {
            Constructor<C> constructor = clazz.getDeclaredConstructor(obj.getClass());
            return constructor.newInstance(obj);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("實例化失敗", e);
        }
    }
}
