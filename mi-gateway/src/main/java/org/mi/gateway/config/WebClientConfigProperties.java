package org.mi.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-06 15:34
 **/
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "web.client")
public class WebClientConfigProperties {

    private String clientId;

    private String clientSecret;

    private String scope;

    private String passwordGrantType;

    private String phoneVerifyCodeGrantType;

    private String socialGrantType;
}
