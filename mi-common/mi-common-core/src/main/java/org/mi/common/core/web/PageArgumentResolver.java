package org.mi.common.core.web;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mi.common.core.constant.PageParamConstant;
import org.mi.common.core.exception.util.AssertUtil;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @program: mi-community
 * @description: 处理分页请求参数,防止不合法的分页参数,防止orderBy sql注入问题
 * @author: Micah
 * @create: 2020-11-15 14:07
 **/
public class PageArgumentResolver implements HandlerMethodArgumentResolver {

    private final static String[] KEYWORDS = { "master", "truncate", "insert", "select", "delete", "update", "declare",
            "alter", "drop", "sleep" };

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Page.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        AssertUtil.notNull(request,"请求对象不能为空");
        String currentPage = request.getParameter(PageParamConstant.CURRENT_PAGE);
        String pageSize = request.getParameter(PageParamConstant.PAGE_SIZE);
        String[] sorts = request.getParameterValues(PageParamConstant.SORT);
        Page<?> page = new Page<>();
        page.setCurrent(StrUtil.isBlank(currentPage) ? PageParamConstant.DEFAULT_PAGE_NUM : Long.parseLong(currentPage) + 1);
        page.setSize(StrUtil.isBlank(pageSize) ? PageParamConstant.DEFAULT_PAGE_SIZE : Long.parseLong(pageSize));
        List<OrderItem> orderItems = new ArrayList<>();
        Optional.ofNullable(sorts).ifPresent(strings -> Arrays.stream(strings)
                .filter(sqlInjectPredicate()).forEach(s -> {
                    String[] sortParams = s.split(",");
                    OrderItem orderItem = new OrderItem();
                    orderItem.setColumn(sortParams[0]);
                    orderItem.setAsc("asc".equalsIgnoreCase(sortParams[1]));
                    orderItems.add(orderItem);
                }));
        page.setOrders(orderItems);
        return page;
    }

    /**
     * 判断用户输入里面有没有关键字
     * @return Predicate
     */
    private Predicate<String> sqlInjectPredicate() {
        return sql -> {
            for (String keyword : KEYWORDS) {
                if (StrUtil.containsIgnoreCase(sql, keyword)) {
                    return false;
                }
            }
            return true;
        };
    }
}
