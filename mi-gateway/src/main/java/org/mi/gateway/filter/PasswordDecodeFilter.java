package org.mi.gateway.filter;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import org.mi.common.core.constant.AuthClientConstant;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.exception.IllegalRequestException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.util.FileUtils;
import org.mi.gateway.config.WebClientConfigProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static org.mi.gateway.util.WebServerUtils.generateNewRequest;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-06 14:46
 **/
@Component
public class PasswordDecodeFilter extends AbstractGatewayFilterFactory<Object> implements InitializingBean{

    private RSA rsa;

    @Resource
    private WebClientConfigProperties webClientConfigProperties;

    @Override
    public GatewayFilter apply(Object config) {
        return new GatewayFilter() {
            private final RSA rsa = PasswordDecodeFilter.this.rsa;

            private final WebClientConfigProperties clientConfigProperties = PasswordDecodeFilter.this.webClientConfigProperties;

            private final Map<String,Object> params = Maps.newHashMapWithExpectedSize(6);

            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                for (String path : SecurityConstant.PASSWORD_DECODE_PATH) {
                    if (StrUtil.containsIgnoreCase(exchange.getRequest().getURI().getPath(),path)){
                        if (!Objects.equals(exchange.getRequest().getMethod(), HttpMethod.POST)) {
                            return Mono.error(new IllegalRequestException("非法访问"));
                        }
                        JSON loginData = (JSON) exchange.getAttributes().get("loginData");
                        String username = (String) loginData.getByPath("username");
                        String password = (String) loginData.getByPath("password");
                        String decodeUsername = null;
                        String decodePassword = null;
                        try {
                            decodeUsername = this.rsa.decryptStr(username, KeyType.PrivateKey, StandardCharsets.UTF_8);
                            decodePassword = this.rsa.decryptStr(password, KeyType.PrivateKey, StandardCharsets.UTF_8);
                        } catch (Exception e) {
                            return Mono.error(new RuntimeException("非法访问"));
                        }
                        AssertUtil.notBlank(decodeUsername, decodePassword);
                        this.params.put(MiUserConstant.USER_NAME, decodeUsername);
                        this.params.put(MiUserConstant.PASSWORD, decodePassword);
                        this.params.put(AuthClientConstant.CLIENT_ID, this.clientConfigProperties.getClientId());
                        this.params.put(AuthClientConstant.CLIENT_SECRET, this.clientConfigProperties.getClientSecret());
                        this.params.put(AuthClientConstant.GRANT_TYPE, this.clientConfigProperties.getPasswordGrantType());
                        this.params.put(AuthClientConstant.SCOPE, this.clientConfigProperties.getScope());
                        String params = HttpUtil.toParams(this.params, StandardCharsets.UTF_8);
                        URI newUri = UriComponentsBuilder.fromUri(exchange.getRequest().getURI()).replaceQuery(params).build(true).toUri();
                        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().uri(newUri).build();
                        return chain.filter(exchange.mutate().request(serverHttpRequest).build());
                    }
                }
                return chain.filter(exchange);
            }
        };
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String privateKey = FileUtils.readFileContent("privateKey.txt");
        AssertUtil.notBlank(privateKey);
        this.rsa = new RSA(privateKey, null);
    }

}
