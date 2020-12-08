package org.mi.security.config;


import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.util.FileUtils;
import org.mi.security.component.CustomUserAuthenticationConverter;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    private final CustomUserAuthenticationConverter customUserAuthenticationConverter;



    @Bean
    public TokenStore jwtTokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(customUserAuthenticationConverter);
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        String publicKey = FileUtils.readFileContent("public.txt");
        jwtAccessTokenConverter.setVerifierKey(StringUtils.isBlank(publicKey) ?
                this.getFromRemoteServer() : publicKey);
        jwtAccessTokenConverter.setAccessTokenConverter(accessTokenConverter);
        return jwtAccessTokenConverter;
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
            JSON publicKey = JSONUtil.parse(pubKey);
            log.info("Get Key From Authorization Server.");
            return publicKey.getByPath("value").toString();
        } catch (Exception e) {
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
