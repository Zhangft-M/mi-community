package org.mi.post.test;

import org.junit.jupiter.api.Test;
import org.mi.biz.post.MiPostApplication;
import org.mi.common.core.constant.RedisCacheConstant;
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

    @Test
    public void sIsMember(){
        Boolean isMember = this.redisUtils.sIsMember(RedisCacheConstant.USER_OWN_POST_ID + 1, 539915499476365312L);
        System.out.println(isMember);
    }
}
