package org.mi.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsUtils;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-10-25 00:22
 **/
@RequiredArgsConstructor
@EnableConfigurationProperties(PermitUrls.class)
public class OauthResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final ResourceServerProperties resourceServerProperties;

    private final AuthenticationEntryPoint anonymousAccessExceptionEntryPoint;

    private final AccessDeniedHandler customAccessDeniedHandler;

    private final PermitUrls permitUrls;

    private final TokenStore jwtTokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(resourceServerProperties.getResourceId())
                .tokenStore(jwtTokenStore)
                .stateless(true)
                .authenticationEntryPoint(anonymousAccessExceptionEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 前后端分离下，可以关闭 csrf
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        permitUrls.getUrls().forEach(url -> registry.antMatchers(url).permitAll());
        registry.requestMatchers(CorsUtils::isPreFlightRequest).permitAll();
        registry.anyRequest().authenticated()
                .and().csrf().disable();
    }
}
