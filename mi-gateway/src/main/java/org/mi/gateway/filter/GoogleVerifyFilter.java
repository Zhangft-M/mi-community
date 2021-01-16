package org.mi.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.IllegalRequestException;
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
 * @create: 2020-12-07 20:02
 **/
@Component
public class GoogleVerifyFilter extends AbstractGatewayFilterFactory<Object> {

    private static final String APP_KEY = "6LdCuPwZAAAAAPD2YgxXDGYyKXJdwQk4CJ7rmzAn";

    private static final String VERIFY_PATH = "https://www.recaptcha.net/recaptcha/api/siteverify";

    @Override
    public GatewayFilter apply(Object config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                String requestPath = exchange.getRequest().getURI().getPath();
                HttpMethod method = exchange.getRequest().getMethod();
                for (String filterPath : SecurityConstant.GOOGLE_CAPTCHA_VERIFY_PATH) {
                    if (StrUtil.containsIgnoreCase(requestPath, filterPath)) {
                        if (!Objects.requireNonNull(method).equals(HttpMethod.POST)) {
                            return Mono.error(new IllegalRequestException("非法请求"));
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
                            if (!isJson) {
                                return Mono.error(new IllegalParameterException("参数格式不正确"));
                            }
                            JSON params = JSONUtil.parse(msg);
                            String verifyData = params.getByPath(SecurityConstant.VERIFY_DATA).toString();
                            try {
                                HttpResponse response = HttpUtil.createPost(VERIFY_PATH)
                                        .form("secret", APP_KEY)
                                        .form("response", verifyData)
                                        .form("remoteip", Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress()).execute();
                                JSON jsonResponse = JSONUtil.parse(response.body());
                                Boolean isSuccess = (Boolean) jsonResponse.getByPath("success");
                                if (!isSuccess) {
                                    return Mono.error(new IllegalRequestException("非法访问"));
                                }
                            } catch (Exception e) {
                                return Mono.error(new IllegalRequestException("非法访问"));
                            }
                            if (StrUtil.containsIgnoreCase(requestPath,SecurityConstant.PASSWORD_LOGIN_PATH)) {
                                JSON loginData = (JSON) params.getByPath("loginData");
                                exchange.getAttributes().put("loginData", loginData);
                                return chain.filter(exchange.mutate().request(generateNewRequest(exchange.getRequest(), bodyBytes)).build());
                            }else if (StrUtil.containsIgnoreCase(requestPath,SecurityConstant.POST_ADD_PATH)) {
                                JSON postData = (JSON) params.getByPath("postData");
                                exchange.getAttributes().put("postData",postData);
                                return chain.filter(exchange.mutate().request(generateNewRequest(exchange.getRequest(), bodyBytes)).build());
                            }
                            return chain.filter(exchange);
                        });
                    }
                }
                return chain.filter(exchange);
            }
        };
    }
}
