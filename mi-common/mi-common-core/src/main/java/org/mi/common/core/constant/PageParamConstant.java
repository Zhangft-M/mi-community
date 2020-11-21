package org.mi.common.core.constant;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-15 14:15
 **/
public interface PageParamConstant {

    String CURRENT_PAGE = "page";

    String PAGE_SIZE = "size";

    String SORT = "sort";

    Long DEFAULT_PAGE_NUM = 1L;

    Long DEFAULT_PAGE_SIZE = 10L;

    String[] DEFAULT_SORTS = new String[]{"id,desc"};
}
