package org.mi.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigRequest;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigResponse;
import com.aliyuncs.exceptions.ClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.IllegalRequestException;
import org.mi.common.core.exception.util.AssertUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
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
 * @create: 2020-12-06 00:19
 **/
@Component
@RequiredArgsConstructor
public class AliyunVerifyFilter extends AbstractGatewayFilterFactory<Object> {

    private final IAcsClient acsClient;

    private final AuthenticateSigRequest authenticateSigRequest;

    @Override
    public GatewayFilter apply(Object config) {
        return new GatewayFilter() {
            private final IAcsClient acsClient = AliyunVerifyFilter.this.acsClient;

            private static final int SUCCESS_CODE = 100;

            private final AuthenticateSigRequest authenticateSigRequest = AliyunVerifyFilter.this.authenticateSigRequest;
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                String requestPath = exchange.getRequest().getURI().getPath();
                HttpMethod method = exchange.getRequest().getMethod();
                if (!Objects.requireNonNull(method).equals(HttpMethod.POST)){
                    return Mono.error(new IllegalRequestException("非法请求"));
                }
                if (!StrUtil.containsIgnoreCase(requestPath,SecurityConstant.PASSWORD_LOGIN_PATH)){
                    return chain.filter(exchange);
                }
                // 对验证参数进行校验
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
                    JSON params = JSONUtil.parse(msg);
                    JSON verifyData = (JSON) params.getByPath(SecurityConstant.VERIFY_DATA);
                    String sessionId = (String) verifyData.getByPath("sessionId");
                    String sig = (String) verifyData.getByPath("sig");
                    String token = (String) verifyData.getByPath("token");
                    AssertUtil.notBlank(sessionId,sig,token);
                    this.authenticateSigRequest.setRemoteIp(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
                    this.authenticateSigRequest.setSig(sig);
                    this.authenticateSigRequest.setSessionId(sessionId);
                    this.authenticateSigRequest.setToken(token);
                    try {
                        AuthenticateSigResponse response = this.acsClient.getAcsResponse(authenticateSigRequest);
                        if (response.getCode() != SUCCESS_CODE){
                            return Mono.error(new IllegalRequestException("非法访问"));
                        }
                    } catch (ClientException e) {
                        return Mono.error(new IllegalRequestException("非法访问"));
                    }
                    JSON loginData = (JSON) params.getByPath("loginData");
                    exchange.getAttributes().put("loginData",loginData);
                    return chain.filter(exchange.mutate().request(generateNewRequest(exchange.getRequest(), loginData.toJSONString(4).getBytes(StandardCharsets.UTF_8))).build());
                });
            }
        };
    }

}
