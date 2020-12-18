package org.mi.post.test;

import org.junit.jupiter.api.Test;
import org.mi.biz.post.MiPostApplication;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.constant.ThumbUpConstant;
import org.mi.common.core.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-22 16:56
 **/
@SpringBootTest(classes = MiPostApplication.class)
public class RedisTest {

    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void queryKeys(){
        Object o = this.redisUtils.get(RedisCacheConstant.VERIFY_CODE_PREFIX + "18986233587");
        System.out.println(o);
    }
}
