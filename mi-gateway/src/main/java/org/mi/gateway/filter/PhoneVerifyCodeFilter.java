package org.mi.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBufAllocator;
import lombok.RequiredArgsConstructor;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.IllegalRequestException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
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
public class PhoneVerifyCodeFilter extends AbstractGatewayFilterFactory<Object> {

    @Autowired
    private RedisUtils redisUtils;


    @Override
    public GatewayFilter apply(Object config) {
        return new GatewayFilter() {

            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                RedisUtils redisUtils = PhoneVerifyCodeFilter.this.redisUtils;
                ServerHttpRequest request = exchange.getRequest();
                String path = request.getURI().getPath();
                if (!StrUtil.containsIgnoreCase(path, SecurityConstant.VERIFY_CODE_LOGIN)) {
                    return chain.filter(exchange);
                }
                if (!HttpMethod.POST.equals(request.getMethod())){
                    return Mono.error(new IllegalRequestException("请求方法不正确"));
                }
                return DataBufferUtils.join(exchange.getRequest().getBody()).map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                }).flatMap(bodyBytes -> {
                    String msg = new String(bodyBytes, StandardCharsets.UTF_8);
                    boolean isJson = JSONUtil.isJson(msg);
                    if (!isJson){
                        return Mono.error(new IllegalParameterException("参数格式不正确"));
                    }
                    JSON param = JSONUtil.parse(msg);
                    String phoneNumber = (String) param.getByPath(MiUserConstant.PHONE_NUMBER);
                    String verifyCode = (String) param.getByPath(MiUserConstant.VERIFY_CODE);
                    AssertUtil.notBlank(phoneNumber,verifyCode);
                    String cacheVerifyCode = (String) redisUtils.get(RedisCacheConstant.VERIFY_CODE_PREFIX + phoneNumber);
                    if (!StrUtil.equals(cacheVerifyCode,verifyCode)){
                        return Mono.error(new IllegalParameterException("验证码错误"));
                    }
                    redisUtils.del(RedisCacheConstant.VERIFY_CODE_PREFIX + phoneNumber);
                    return chain.filter(exchange.mutate().request(generateNewRequest(exchange.getRequest(), bodyBytes)).build());
                });
            }
        };
    }

}
