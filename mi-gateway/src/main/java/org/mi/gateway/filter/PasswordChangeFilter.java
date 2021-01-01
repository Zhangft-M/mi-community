package org.mi.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.IllegalRequestException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.gateway.util.WebServerUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
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
 * @create: 2020-12-27 18:00
 **/
@Slf4j
@Component
public class PasswordChangeFilter extends AbstractGatewayFilterFactory<Object> {
    @Override
    public GatewayFilter apply(Object config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                String requestPath = exchange.getRequest().getURI().getPath();
                if (!StrUtil.containsIgnoreCase(requestPath, SecurityConstant.PASSWORD_CHANGE_PATH)) {
                    return chain.filter(exchange);
                }
                if (!Objects.equals(exchange.getRequest().getMethod(),HttpMethod.PUT)){
                    return Mono.error(new IllegalRequestException("非法请求"));
                }
                return DataBufferUtils.join(exchange.getRequest().getBody()).map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                }).flatMap(bodyBytes -> {
                    String msg = new String(bodyBytes, StandardCharsets.UTF_8);
                    if (!JSONUtil.isJson(msg)) {
                        return Mono.error(new IllegalParameterException("参数格式不正确"));
                    }
                    JSON params = JSONUtil.parse(msg);
                    exchange.getAttributes().put(MiUserConstant.PASSWORD, params.getByPath(MiUserConstant.PASSWORD,String.class));
                    return chain.filter(exchange.mutate().request(generateNewRequest(exchange.getRequest(), params.toJSONString(0).getBytes(StandardCharsets.UTF_8))).build());
                });
            }
        };
    }
}
