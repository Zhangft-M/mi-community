package org.mi.common.core.web.security;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mi.common.core.util.RedisUtils;
import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2021-01-25 20:59
 **/
@Aspect
@RequiredArgsConstructor
public class IdempotentAspect implements Ordered {

    private final HttpServletRequest request;

    private static String token;

    private final RedisUtils redisUtils;

    @SneakyThrows
    @Around("@annotation(Idempotent)")
    public Object idempotentAspect(ProceedingJoinPoint pjp){
        // 由于解决网关的一个bug不得以将参数拼接在url后面
        String value = this.request.getQueryString();
        if (StrUtil.isBlank(value)){
            value = this.request.getParameter("title") + this.request.getParameter("content");
        }
        synchronized (this){
            token = MD5.create().digestHex(value, StandardCharsets.UTF_8);
            if (this.redisUtils.hasKey(token)){
                throw new RuntimeException("请勿重复提交请求");
            }
            this.redisUtils.setIfNotExit(token);
            return pjp.proceed();
        }
    }

    @AfterReturning("@annotation(Idempotent)")
    public void afterReturningAspect(){
        if (StrUtil.isNotBlank(token)){
            if (this.redisUtils.hasKey(token)){
                this.redisUtils.del(token);
            }
        }
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
