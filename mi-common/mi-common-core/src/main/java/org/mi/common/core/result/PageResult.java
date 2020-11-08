package org.mi.common.core.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @program: mi-community
 * @description: 分页结果集
 * @author: Micah
 * @create: 2020-10-22 14:49
 **/
@Setter
@Getter
public class PageResult {
    /**
     * 总条数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long page;

    /**
     * 数据结果集
     */
    private List<?> records;

    public PageResult() {
    }

    private PageResult(Long total, Long page, List<?> records) {
        this.total = total;
        this.page = page;
        this.records = records;
    }

    private PageResult(Long total, List<?> records) {
        this.total = total;
        this.records = records;
    }

    public static PageResult of(Long total, Long page, List<?> records){
        return new PageResult(total,page,records);
    }

    public static PageResult of(Long total, List<?> records){
        return new PageResult(total,records);
    }
}
