package org.mi.gateway.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.mi.gateway.mapper.RouterMapper;
import org.mi.gateway.model.Filter;
import org.mi.gateway.model.Router;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;

/**
 * @program: mi-community
 * @description: 路由加载器
 * @author: Micah
 * @create: 2020-12-03 18:02
 **/
@Component
@AllArgsConstructor
public class RouterLoader implements ApplicationEventPublisherAware, InitializingBean {

    private ApplicationEventPublisher eventPublisher;

    private final RouterMapper routerMapper;

    private final RouteDefinitionWriter definitionWriter;

    private static final Map<String,String> DEFAULT_ARGS = new HashMap<>();

    private final Set<GatewayFlowRule> rules = new HashSet<>();

    static {
        DEFAULT_ARGS.put("N/A","N/A");
    }



    @Override
    public void afterPropertiesSet() throws Exception {
        this.initRouters();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }


    /**
     * 加载路由
     */
    private void initRouters() {
        List<Router> routers = this.routerMapper.selectAll();
        for (Router router : routers) {
            // 判断是否启用该路由
            if (router.getEnable()){
                this.loadRoute(router);
            }
        }
        this.eventPublisher.publishEvent(new RefreshRoutesEvent(this));
        GatewayRuleManager.loadRules(rules);
    }

        /**
         * 配置路由，断言和过滤器都是用shortcut形式配置，与数据库对应
         * @param router
         */
        private void loadRoute(Router router) {
            RouteDefinition definition = new RouteDefinition();
            List<PredicateDefinition> predicateDefinitions = new ArrayList<>();
            List<FilterDefinition> filterDefinitions = new ArrayList<>();
            // 判断路由状况
            URI uri = null;
            if (router.getRouterType().equals(0)) {
                // 从服务注册中心获取uri
                uri = UriComponentsBuilder.fromUriString("lb://" + router.getRouterUrl()).build().toUri();
            } else {
                // 直接获取httpUri
                uri = UriComponentsBuilder.fromHttpUrl(router.getRouterUrl()).build().toUri();
            }
            /**
             * spring:
             *   cloud:
             *     gateway:
             *       routes:
             *         - id: mygateway
             *           uri: lb://test-serve
             *           filters:
             *             - StripPrefix=1 // 过滤掉服务名后面的第一个前缀,(去掉这个前缀名，再转发)
             *           predicates:
             *             - Path=/test/**
             */
            definition.setId(router.getRouterId());
            // 设置uri
            definition.setUri(uri);
            // 设置- Path=/test/**
            if (CollUtil.isNotEmpty(router.getPredicates())){
                router.getPredicates().forEach(predicate -> {
                    // 实例化断言定义对象
                    PredicateDefinition predicateDefinition = new PredicateDefinition();
                    // 设置断言的名称
                    predicateDefinition.setName(predicate.getPredicateName());
                    String[] preValues = predicate.getPredicateVal().split(",");
                    Map<String, String> valueMap = initArgs(preValues);
                    // 设置断言的值
                    predicateDefinition.setArgs(valueMap);
                    predicateDefinitions.add(predicateDefinition);
                });
            }else {
                PredicateDefinition predicateDefinition = new PredicateDefinition();
                predicateDefinition.setName("Path");
                predicateDefinition.setArgs(DEFAULT_ARGS);
                predicateDefinitions.add(predicateDefinition);
            }
            if (CollUtil.isNotEmpty(router.getFilters())){
                for (Filter filter : router.getFilters()) {
                    FilterDefinition filterDefinition = new FilterDefinition();
                    filterDefinition.setName(filter.getFilterName());
                    // 判断是否为自定义的过滤器
                    if (StrUtil.isNotBlank(filter.getFilterVal())){
                        String[] values = filter.getFilterVal().split(",");
                        Map<String, String> valueMap = initArgs(values);
                        filterDefinition.setArgs(valueMap);
                    }
                    filterDefinitions.add(filterDefinition);
                }
            }else {
                FilterDefinition filterDefinition = new FilterDefinition();
                filterDefinition.setName("N/A");
            }
            // 设置predicates
            definition.setPredicates(predicateDefinitions);
            // 设置filters
            definition.setFilters(filterDefinitions);
            this.definitionWriter.save(Mono.just(definition)).subscribe();
            // 设置限流规则
            rules.add(new GatewayFlowRule(router.getRouterId())
                    // 限流阈值
                    .setCount(router.getThreshold())
                    // 统计时间窗口，限流后一秒之类是不能访问的
                    .setIntervalSec(router.getIntervalSec()));
            // 还可以配置其他参数，需要在数据库添加相应的字段
        }

    /**
     * 初始化断言或者过滤器的参数
     * @param values
     * @return
     */
    private Map<String, String> initArgs(String[] values) {
        Map<String, String> valueMap = Maps.newHashMapWithExpectedSize(values.length);
        for (int i = 0; i < values.length; i++) {
            valueMap.put(NameUtils.GENERATED_NAME_PREFIX + i, values[i]);
        }
        return valueMap;
    }



}
