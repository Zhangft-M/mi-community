package org.mi.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * @program: mi-community
 * @description: 认证服务配置
 * @author: Micah
 * @create: 2020-10-24 22:55
 **/
@Configuration
@RequiredArgsConstructor
@EnableAuthorizationServer
public class Oauth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final ClientDetailsService customClientDetailsService;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final TokenStore redisTokenStore;

    private final TokenEnhancer customTokenEnhancer;

    private final PasswordEncoder passwordEncoder;

    private final JwtAccessTokenConverter jwtAccessTokenConverter;



    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients()
                .checkTokenAccess("permitAll()")
                .tokenKeyAccess("permitAll()")
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(customClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET,HttpMethod.POST)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .tokenStore(redisTokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)
                .tokenEnhancer(customTokenEnhancer)
                // 拒绝重复使用refreshToken
                .reuseRefreshTokens(false);
    }

}
