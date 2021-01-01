package org.mi.security.component;

import cn.hutool.core.collection.CollUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.util.RedisUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;
import sun.security.util.SecurityConstants;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class OAuth2FeignRequestInterceptor implements RequestInterceptor {

    private final RedisUtils redisUtils;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 获取头信息
        Collection<String> fromHeader = requestTemplate.headers().get(SecurityConstant.FROM);
        if (CollUtil.isNotEmpty(fromHeader) && fromHeader.contains(SecurityConstant.FROM_IN)){
            // 使用了Inner注解，直接放行
            // 在请求头中添加唯一的请求凭证
            String requestCertificate = UUID.randomUUID().toString();
            this.redisUtils.set(requestCertificate,requestCertificate,15, TimeUnit.MINUTES);
            requestTemplate.header(SecurityConstant.INNER_REQUEST_CERTIFICATE,requestCertificate);
            return;
        }
        // 在头信息添加token

        // 获取安全之类信息的上下文
        SecurityContext context = SecurityContextHolder.getContext();
        // 获取认证信息
        Authentication authentication = context.getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof OAuth2AuthenticationDetails){
            // 获取OAuth2AuthenticationDetails对象
            OAuth2AuthenticationDetails dateils = (OAuth2AuthenticationDetails) authentication.getDetails();
            // 在头信息添加token
            requestTemplate.header(HttpHeaders.AUTHORIZATION,
                    String.format("%s %s", SecurityConstant.BEARER_TOKEN_TYPE, dateils.getTokenValue()));
        }

    }
}