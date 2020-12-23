package org.mi.security.aspect;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.util.RedisUtils;
import org.mi.security.annotation.Inner;
import org.springframework.core.Ordered;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-30 17:20
 **/
@Slf4j
@Aspect
@RequiredArgsConstructor
public class InnerAspect implements Ordered {

    private final HttpServletRequest request;

    private final RedisUtils redisUtils;

    @Around("@annotation(inner)")
    @SneakyThrows
    public Object innerAround(ProceedingJoinPoint pjp, Inner inner){
        String header = request.getHeader(SecurityConstant.FROM);
        if (inner.value() && !StrUtil.equals(SecurityConstant.FROM_IN, header)) {
            log.warn("访问接口 {} 没有权限", pjp.getSignature().getName());
            throw new AccessDeniedException("Access is denied");
        }
        String requestCertificate = request.getHeader(SecurityConstant.INNER_REQUEST_CERTIFICATE);
        Object certificate = this.redisUtils.get(requestCertificate);
        if (null == certificate) {
            log.warn("访问接口 {} 没有权限", pjp.getSignature().getName());
            throw new AccessDeniedException("Access is denied");
        }
        this.redisUtils.del(requestCertificate);
        return pjp.proceed();
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
