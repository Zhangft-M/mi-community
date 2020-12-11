package org.mi.security.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.exception.util.AssertUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-28 16:36
 **/
@Slf4j
@UtilityClass
public class SecurityContextHelper {

    public static Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Class<?> clazz = authentication.getPrincipal().getClass();
        try {
            Method getUserId = clazz.getMethod("getUserId", Long.TYPE);
            Long userId = (Long) getUserId.invoke(authentication.getPrincipal(), null);
            AssertUtil.idIsNotNull(userId);
            return userId;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("没有获取到用户相关的信息");
        }
        return null;
    }


}
