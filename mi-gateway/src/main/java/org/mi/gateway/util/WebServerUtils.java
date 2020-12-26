package org.mi.gateway.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBufAllocator;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.util.AssertUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-06 00:39
 **/
public class WebServerUtils {

    public static ServerHttpRequest generateNewRequest(ServerHttpRequest request, byte[] bytes) {
        URI ex = UriComponentsBuilder.fromUri(request.getURI()).build(true).toUri();
        ServerHttpRequest newRequest = request.mutate().uri(ex).build();
        DataBuffer dataBuffer = stringBuffer(bytes);
        Flux<DataBuffer> flux = Flux.just(dataBuffer);
        newRequest = new ServerHttpRequestDecorator(newRequest) {
            @Override
            public Flux<DataBuffer> getBody() {
                return flux;
            }
        };
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
        AssertUtil.notBlank(phoneNumber,verifyCode);
        if (!StrUtil.equals(cacheVerifyCode,verifyCode)){
            throw new IllegalParameterException("验证码错误");
        }
        redisTemplate.delete(RedisCacheConstant.VERIFY_CODE_PREFIX + phoneNumber);
        return param;
    }
}
