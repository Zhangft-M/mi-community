package org.mi.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-30 17:19
 **/
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inner {
    /**
     * 是否放行
     */
    boolean value() default true;

    /**
     * 需要特殊判空的字段(预留)
     * @return {}
     */
    String[] field() default {};
}
