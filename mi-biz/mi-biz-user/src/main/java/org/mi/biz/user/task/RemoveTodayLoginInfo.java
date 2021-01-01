package org.mi.biz.user.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.util.RedisUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-21 19:12
 **/
@Slf4j
// @Component
// @EnableScheduling
@RequiredArgsConstructor
public class RemoveTodayLoginInfo {

    private final RedisUtils redisUtils;

    @Scheduled(cron = "0 0 0 * * ?")
    public void removeTodayLoginInfo() {
        log.info("-------------------------开始删除redis中今天已经登录的用户,开始时间为={}-----------------------------------", LocalDateTime.now());
        this.redisUtils.del(RedisCacheConstant.USER_TODAY_HAS_LOGIN + "*");
        log.info("-------------------------删除完毕,结束时间为={}-----------------------------------", LocalDateTime.now());
    }
}
