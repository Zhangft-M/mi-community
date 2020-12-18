package org.mi.gateway.test;

import org.junit.jupiter.api.Test;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.util.RedisUtils;
import org.mi.gateway.MiGatewayApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-18 20:38
 **/
@SpringBootTest(classes = MiGatewayApplication.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void getTest(){
        Object o = this.redisTemplate.opsForValue().get(RedisCacheConstant.VERIFY_CODE_PREFIX + "18986233587");
        System.out.println(o);
    }
}
