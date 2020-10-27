package org.common.mp.util;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.experimental.UtilityClass;
import org.common.mp.component.PageParam;


import java.util.Collections;


/**
 * @program: mi-community
 * @description: 分页工具类
 * @author: Micah
 * @create: 2020-10-22 23:05
 **/
@UtilityClass
public class PageUtils {

    private static final String ASC = "asc";

    /*public static <T> Page<T> ofPage(Pageable pageable){
        Page<T> page = new Page<>(pageable.getPageNumber() - 1,pageable.getPageSize());
        Sort sort = pageable.getSort();
        List<OrderItem> orderItems = sort.stream().map(it -> new OrderItem(it.getProperty(), it.getDirection().isAscending())).collect(Collectors.toList());
        page.setOrders(orderItems);
        return page;
    }*/

    public static <T>Page<T> ofPage(PageParam param){
        Page<T> page = new Page<>(param.getCurrentPage(), param.getSize());
        String[] sorts = param.getSort().split(",");
        OrderItem orderItem = new OrderItem();
        orderItem.setColumn(sorts[0]);
        orderItem.setAsc(ASC.equalsIgnoreCase(sorts[1]));
        page.setOrders(Collections.singletonList(orderItem));
        return page;
    }
}
