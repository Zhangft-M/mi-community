package org.mi.common.core.util;

import org.mi.common.core.exception.util.AssertUtil;
import org.springframework.context.ApplicationContext;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-27 18:38
 **/
public class SpringContextUtils {

    public static <T> T getBean(ApplicationContext applicationContext,String beanName,Class<T> clazz){
        T bean = applicationContext.getBean(beanName, clazz);
        AssertUtil.notNull(bean);
        return bean;
    }
}
