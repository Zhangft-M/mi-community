package org.mi.security.config;

import cn.hutool.core.util.ReUtil;
import lombok.Getter;
import lombok.Setter;
import org.mi.security.annotation.Anonymous;
import org.mi.security.annotation.Inner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-30 19:07
 **/
@RefreshScope
@ConfigurationProperties(prefix = "security.oauth2.ignore")
public class PermitUrls implements InitializingBean, ApplicationContextAware {

    /**
     * 匹配url中的{参数名}，将其转化为*
     */
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    private ApplicationContext applicationContext;

    @Setter
    @Getter
    private List<String> urls = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        // 获取controller类
        RequestMappingHandlerMapping handlerMapping = this.applicationContext.getBean(RequestMappingHandlerMapping.class);
        // RequestMappingInfo为方法上面的RequestMapping类容
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        handlerMethods.keySet().forEach(info -> {
            // 获取接口方法
            HandlerMethod handlerMethod = handlerMethods.get(info);
            // 获取方法上面的Inner注解
            Inner inner = handlerMethod.getMethodAnnotation(Inner.class);
            Optional.ofNullable(inner).ifPresent(inner1 -> {
                info.getPatternsCondition().getPatterns().forEach(url->{
                    urls.add(ReUtil.replaceAll(url, PATTERN, "*"));
                });
            });
            Anonymous anonymous = handlerMethod.getMethodAnnotation(Anonymous.class);
            Optional.ofNullable(anonymous).ifPresent(
                    anonymous1 -> info.getPatternsCondition().getPatterns().forEach(
                            url-> urls.add(ReUtil.replaceAll(url, PATTERN, "*"))));
            // 获取类上边的注解, 替代path variable 为 *
            Inner controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Inner.class);
            Optional.ofNullable(controller).ifPresent(inner1 -> info.getPatternsCondition().getPatterns()
                    .forEach(url -> urls.add(ReUtil.replaceAll(url, PATTERN, "*"))));
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
