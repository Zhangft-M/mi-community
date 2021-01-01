package org.mi.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.mi.common.core.constant.AuthClientConstant;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.gateway.config.WebClientConfigProperties;
import org.mi.gateway.util.WebServerUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @program: mi-community
 * @description: 手机验证码转发登录过滤器
 * @author: Micah
 * @create: 2020-12-18 22:41
 **/
@Component
@Deprecated
@RequiredArgsConstructor
public class PhoneLoginForwardFilter extends AbstractGatewayFilterFactory<Object> {

    private final WebClientConfigProperties webClientConfigProperties;


    @Override
    public GatewayFilter apply(Object config) {

        return new GatewayFilter() {
            private final WebClientConfigProperties clientConfigProperties = PhoneLoginForwardFilter.this.webClientConfigProperties;
            private final Map<String,Object> params = Maps.newHashMapWithExpectedSize(5);
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                if (!StrUtil.containsIgnoreCase(exchange.getRequest().getURI().getPath(), SecurityConstant.VERIFY_CODE_LOGIN)){
                    return chain.filter(exchange);
                }
                String phoneNumber = String.valueOf(exchange.getAttributes().get(MiUserConstant.PHONE_NUMBER));
                this.params.put(StrUtil.toCamelCase(AuthClientConstant.CLIENT_ID), this.clientConfigProperties.getClientId());
                this.params.put(StrUtil.toCamelCase(AuthClientConstant.CLIENT_SECRET), this.clientConfigProperties.getClientSecret());
                this.params.put(StrUtil.toCamelCase(AuthClientConstant.GRANT_TYPE), this.clientConfigProperties.getPhoneVerifyCodeGrantType());
                this.params.put(AuthClientConstant.SCOPE, this.clientConfigProperties.getScope());
                this.params.put(MiUserConstant.PHONE_NUMBER,phoneNumber);
                ServerHttpRequest newRequest = WebServerUtils.generateNewRequest(this.params, exchange);
                return chain.filter(exchange.mutate().request(newRequest).build());
            }
        };
    }
}
