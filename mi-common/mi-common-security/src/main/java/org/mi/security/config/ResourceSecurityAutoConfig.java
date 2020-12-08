package org.mi.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-10-25 01:25
 **/
@ComponentScan({"org.mi.security.component"})
@Import({OauthResourceServerConfig.class,OauthResourceTokenConfig.class})
public class ResourceSecurityAutoConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
