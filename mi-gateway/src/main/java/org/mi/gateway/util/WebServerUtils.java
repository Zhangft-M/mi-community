package org.mi.gateway.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.util.AssertUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-06 00:39
 **/
@Slf4j
public class WebServerUtils {


    private static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(5,new CustomizableThreadFactory("gateway-scheduled-thread-"));

    public synchronized static ServerHttpRequest generateNewRequest(ServerHttpRequest request, byte[] bytes) {
        URI ex = UriComponentsBuilder.fromUri(request.getURI()).build(true).toUri();
        ServerHttpRequest oldRequest = request.mutate().uri(ex).method(Objects.requireNonNull(request.getMethod())).build();
        DataBuffer dataBuffer = stringBuffer(bytes);
        ServerHttpRequest newRequest = new ServerHttpRequestDecorator(oldRequest) {
            @Override
            public Flux<DataBuffer> getBody() {
                return Flux.just(dataBuffer);
            }
        };
        executorService.schedule(()->{
            log.info("释放缓冲流");
            DataBufferUtils.release(dataBuffer);
        },3, TimeUnit.SECONDS);
        newRequest.getHeaders().clearContentHeaders();
        // DataBufferUtils.release(dataBuffer);
        return newRequest;
    }

    private static DataBuffer stringBuffer(byte[] bytes) {
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        return nettyDataBufferFactory.wrap(bytes);
    }

    public static JSON checkVerifyCode(String msg, StringRedisTemplate redisTemplate){
        boolean isJson = JSONUtil.isJson(msg);
        if (!isJson){
            throw new IllegalParameterException("参数格式不正确");
        }
        JSON param = JSONUtil.parse(msg);
        String phoneNumber =  String.valueOf(param.getByPath(MiUserConstant.PHONE_NUMBER));
        String verifyCode =  String.valueOf(param.getByPath(MiUserConstant.VERIFY_CODE));
        String cacheVerifyCode = String.valueOf(redisTemplate.opsForValue().get(RedisCacheConstant.VERIFY_CODE_PREFIX + phoneNumber));
        if (cacheVerifyCode == null) {
            throw new IllegalParameterException("验证码已经过期,请重新输入");
        }
        AssertUtil.notBlank(phoneNumber,verifyCode);
        if (!StrUtil.equals(cacheVerifyCode,verifyCode)){
            throw new IllegalParameterException("验证码错误");
        }
        redisTemplate.delete(RedisCacheConstant.VERIFY_CODE_PREFIX + phoneNumber);
        return param;
    }

    public static ServerHttpRequest generateNewRequest(Map<String,Object> map, ServerWebExchange exchange) {
        String params = HttpUtil.toParams(map, StandardCharsets.UTF_8);
        URI newUri = UriComponentsBuilder.fromUri(exchange.getRequest().getURI()).replaceQuery(params).build(true).toUri();
        return exchange.getRequest().mutate().uri(newUri).build();
    }

}
