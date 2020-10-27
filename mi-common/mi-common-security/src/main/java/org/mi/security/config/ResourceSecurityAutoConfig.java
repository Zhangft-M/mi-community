package org.mi.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-10-25 01:25
 **/
@ComponentScan("org.mi.security.component")
@Import({OauthResourceServerConfig.class,OauthResourceTokenConfig.class})
public class ResourceSecurityAutoConfig {

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
