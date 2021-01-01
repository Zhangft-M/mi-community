package org.mi.gateway.filter;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.constant.AuthClientConstant;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.exception.IllegalRequestException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.util.FileUtils;
import org.mi.common.core.util.SpringContextUtils;
import org.mi.gateway.component.abs.AbstractParamHandler;
import org.mi.gateway.config.WebClientConfigProperties;
import org.mi.gateway.model.enu.ParamHandlerEnum;
import org.mi.gateway.util.WebServerUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static org.mi.gateway.util.WebServerUtils.generateNewRequest;

/**
 * @program: mi-community
 * @description: 参数处理过滤器,处理请求的参数，进行封装,再生成新的请求对象
 * @author: Micah
 * @create: 2020-12-06 14:46
 **/
@Slf4j
@Component
public class RequestParamsHandleFilter extends AbstractGatewayFilterFactory<Object> implements ApplicationContextAware {


    private ApplicationContext applicationContext;


    @Override
    public GatewayFilter apply(Object config) {
        return new GatewayFilter() {

            private final ApplicationContext applicationContext = RequestParamsHandleFilter.this.applicationContext;


            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                for (String path : SecurityConstant.DEAL_REQUEST_PARAMS_PATHS) {
                    String requestPath = exchange.getRequest().getURI().getPath();
                    if (StrUtil.containsIgnoreCase(requestPath,path)){
                        /*if (!Objects.equals(exchange.getRequest().getMethod(), HttpMethod.POST )) {
                            return Mono.error(new IllegalRequestException("非法访问"));
                        }*/
                        // 使用策略模式重构请求参数封装
                        String beanName = ParamHandlerEnum.geBeanName(path);
                        AssertUtil.notNull(beanName);
                        Map<String, Object> params = null;
                        try {
                            AbstractParamHandler paramHandler = SpringContextUtils.getBean(this.applicationContext, beanName, AbstractParamHandler.class);
                            params = paramHandler.dealAndPackageParams(exchange.getAttributes());
                        } catch (Exception e) {
                            log.warn("处理参数失败:=>{}",e.toString());
                            return Mono.error(new IllegalRequestException(e.getMessage()));
                        }
                        ServerHttpRequest newRequest = generateNewRequest(params, exchange);
                        return chain.filter(exchange.mutate().request(newRequest).build());
                    }
                }
                return chain.filter(exchange);
            }
        };
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
