package me.zhengjie.modules.stat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页结果包装类
 * @param <T> 数据类型
 * @author system
 * @date 2025-09-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    
    /**
     * 数据内容
     */
    private List<T> content;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 页大小
     */
    private Integer size;
    
    /**
     * 总记录数
     */
    private Long totalElements;
    
    /**
     * 总页数
     */
    private Integer totalPages;
    
    /**
     * 是否为第一页
     */
    private Boolean first;
    
    /**
     * 是否为最后一页
     */
    private Boolean last;
    
    /**
     * 从Spring Data Page对象构建PageResult
     * @param page Spring Data Page对象
     * @param <T> 数据类型
     * @return PageResult对象
     */
    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}
