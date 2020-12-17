package org.mi.security.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.exception.UnauthorizedException;
import org.mi.common.core.exception.util.AssertUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

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
        try {
            Map<String,String> principal = (Map<String, String>) authentication.getPrincipal();
            Long userId = Long.valueOf(principal.get(MiUserConstant.USER_ID));
            AssertUtil.idIsNotNull(userId);
            return userId;
        } catch (Exception e) {
            log.error("没有获取到用户相关的信息");
            throw new UnauthorizedException();
        }
    }


}
