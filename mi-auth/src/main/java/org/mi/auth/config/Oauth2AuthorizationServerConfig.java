package org.mi.auth.config;

import lombok.RequiredArgsConstructor;
import org.mi.api.user.api.MiUserRemoteApi;
import org.mi.auth.component.VerifyCodeTokenGranter;
import org.mi.common.core.util.RedisUtils;
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
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.List;

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

    private final RedisUtils redisUtils;

    private final UserDetailsService userDetailsService;

    private final MiUserRemoteApi miUserRemoteApi;

    private final TokenStore jwtTokenStore;

    private final TokenEnhancer tokenEnhancer;

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
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .tokenStore(jwtTokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)
                .tokenEnhancer(tokenEnhancer)
                // 拒绝重复使用refreshToken
                .reuseRefreshTokens(false)
                .tokenGranter(tokenGranter());
    }

    @Bean
    public OAuth2RequestFactory oAuth2RequestFactory(){
        return new DefaultOAuth2RequestFactory(customClientDetailsService);
    }

    @Bean
    public TokenGranter tokenGranter(){
        return new CompositeTokenGranter(getCustomizedTokenGranters());
    }

    @Bean
    public DefaultTokenServices customTokenService(){
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(jwtTokenStore);
        tokenServices.setAuthenticationManager(authenticationManager);
        tokenServices.setSupportRefreshToken(false);
        tokenServices.setTokenEnhancer(tokenEnhancer);
        tokenServices.setClientDetailsService(customClientDetailsService);
        return tokenServices;
    }

    private List<TokenGranter> getCustomizedTokenGranters() {
        RefreshTokenGranter refreshTokenGranter = new RefreshTokenGranter(customTokenService(), customClientDetailsService, oAuth2RequestFactory());
        ImplicitTokenGranter implicit = new ImplicitTokenGranter(customTokenService(), customClientDetailsService, oAuth2RequestFactory());
        ClientCredentialsTokenGranter clientCredentialsTokenGranter = new ClientCredentialsTokenGranter(customTokenService(), customClientDetailsService, oAuth2RequestFactory());
        VerifyCodeTokenGranter verifyCodeTokenGranter = new VerifyCodeTokenGranter(customTokenService(),customClientDetailsService,oAuth2RequestFactory(), miUserRemoteApi, redisUtils);
        // 设置返回refresh code
        clientCredentialsTokenGranter.setAllowRefresh(true);

        List<TokenGranter> tokenGranters = new ArrayList<>();
        tokenGranters.add(refreshTokenGranter);
        tokenGranters.add(implicit);
        tokenGranters.add(clientCredentialsTokenGranter);
        tokenGranters.add(verifyCodeTokenGranter);
        if (authenticationManager != null) {
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, customTokenService(), customClientDetailsService, oAuth2RequestFactory()));
        }

        return tokenGranters;
    }
}

