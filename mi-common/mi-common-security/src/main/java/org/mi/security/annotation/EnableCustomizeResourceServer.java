package org.mi.security.annotation;

import org.mi.security.config.ResourceSecurityAutoConfig;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import java.lang.annotation.*;

/**
 * @author micah
 */
@Documented
@Inherited
// 开启资源服务器自动配置
@EnableResourceServer
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
// 开启权限校验注解
@EnableGlobalMethodSecurity(prePostEnabled = true)
// 在spring初始化的时候自动装配这个类
@Import(ResourceSecurityAutoConfig.class)
public @interface EnableCustomizeResourceServer {
}