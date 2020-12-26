package org.mi.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.IllegalRequestException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.gateway.util.WebServerUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.mi.gateway.util.WebServerUtils.generateNewRequest;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-26 23:03
 **/
@Component
@RequiredArgsConstructor
public class RegisterFilter extends AbstractGatewayFilterFactory<Object> {

    private final StringRedisTemplate redisTemplate;

    @Override
    public GatewayFilter apply(Object config) {
        return new GatewayFilter() {
            private final StringRedisTemplate redisTemplate = RegisterFilter.this.redisTemplate;
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                String requestPath = exchange.getRequest().getURI().getPath();
                if (!StrUtil.containsIgnoreCase(requestPath, SecurityConstant.REGISTER_PATH)) {
                    return chain.filter(exchange);
                }
                if (!Objects.equals(exchange.getRequest().getMethod(), HttpMethod.POST)) {
                    return Mono.error(new IllegalRequestException("非法请求"));
                }
                return DataBufferUtils.join(exchange.getRequest().getBody()).map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                }).flatMap(bodyBytes -> {
                    String msg = new String(bodyBytes, StandardCharsets.UTF_8);
                    JSON param = null;
                    try {
                        param = WebServerUtils.checkVerifyCode(msg, this.redisTemplate);
                    } catch (Exception e) {
                        return Mono.error(new IllegalRequestException(e.getMessage()));
                    }
                    exchange.getAttributes().put(MiUserConstant.REGISTER_PARAM,param);
                    return chain.filter(exchange.mutate().request(generateNewRequest(exchange.getRequest(), param.toJSONString(0).getBytes(StandardCharsets.UTF_8))).build());
                });
            }
        };
    }
}
