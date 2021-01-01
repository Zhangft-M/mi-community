package org.mi.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBufAllocator;
import lombok.RequiredArgsConstructor;
import org.mi.common.core.constant.AuthClientConstant;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.IllegalRequestException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.util.RedisUtils;
import org.mi.gateway.config.WebClientConfigProperties;
import org.mi.gateway.util.WebServerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.mi.gateway.util.WebServerUtils.generateNewRequest;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-03 21:02
 **/
@Component
@RequiredArgsConstructor
public class PhoneLoginVerifyCodeFilter extends AbstractGatewayFilterFactory<Object> {

    private final StringRedisTemplate redisTemplate;


    @Override
    public GatewayFilter apply(Object config) {
        return new GatewayFilter() {
            private final StringRedisTemplate redisTemplate = PhoneLoginVerifyCodeFilter.this.redisTemplate;
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                ServerHttpRequest request = exchange.getRequest();
                String path = request.getURI().getPath();
                if (!StrUtil.containsIgnoreCase(path, SecurityConstant.VERIFY_CODE_LOGIN)) {
                    return chain.filter(exchange);
                }
                if (!HttpMethod.POST.equals(request.getMethod())) {
                    return Mono.error(new IllegalRequestException("请求方法不正确"));
                }
                return DataBufferUtils.join(exchange.getRequest().getBody()).map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                }).flatMap(bodyBytes -> {
                    String msg = new String(bodyBytes, StandardCharsets.UTF_8);
                    JSON params = null;
                    try {
                        params = WebServerUtils.checkVerifyCode(msg, this.redisTemplate);
                    } catch (Exception e) {
                        return Mono.error(new IllegalRequestException(e.getMessage()));
                    }
                    AssertUtil.notNull(params);
                    exchange.getAttributes().put(MiUserConstant.PHONE_NUMBER, String.valueOf(params.getByPath(MiUserConstant.PHONE_NUMBER)));
                    return chain.filter(exchange.mutate().request(generateNewRequest(exchange.getRequest(), params.toJSONString(0).getBytes(StandardCharsets.UTF_8))).build());
                });

            }
        };
    }

}
