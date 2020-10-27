package org.mi.security.config;


import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-10-25 00:27
 **/
@Slf4j
@Configuration
@RequiredArgsConstructor
public class OauthResourceTokenConfig {

    private final ResourceServerProperties resourceServerProperties;

    private final ObjectMapper objectMapper;



    @Bean
    public TokenStore jwtTokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setVerifierKey(this.getPubKey());
        return jwtAccessTokenConverter;
    }

    private String getPubKey() {
        return StringUtils.isBlank(resourceServerProperties.getJwt().getKeyValue()) ?
                this.getFromRemoteServer() : resourceServerProperties.getJwt().getKeyValue();
    }

    /**
     * 从授权认证服务获取公钥
     * @return
     */
    private String getFromRemoteServer() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, encodeClient());
        HttpEntity<String> requestEntity = new HttpEntity<>(null, httpHeaders);
        String pubKey = new RestTemplate()
                .getForObject(resourceServerProperties.getJwt().getKeyUri(), String.class, requestEntity);
        // JSONObject body = JSONObject.parseObject(pubKey);
        try {
            JSONObject body = objectMapper.readValue(pubKey, JSONObject.class);
            log.info("Get Key From Authorization Server.");
            return body.getStr("value");
        } catch (IOException e) {
            log.error("Get public key error: {}", e.getMessage());
        }
        return null;


    }

    /**
     * 客户端信息
     *
     * @return basic
     */
    private String encodeClient() {
        return "Basic " + Base64.getEncoder().encodeToString((resourceServerProperties.getClientId()
                + ":" + resourceServerProperties.getClientSecret()).getBytes());
    }


}
