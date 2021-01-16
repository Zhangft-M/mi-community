package org.mi.biz.user.task;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mi.api.user.entity.MiUser;
import org.mi.api.user.entity.UserOwnPost;
import org.mi.biz.user.service.IMiUserService;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.util.RedisUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2021-01-01 18:22
 **/
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class SyncUserInfoTask {

    private final RedisUtils redisUtils;

    private final IMiUserService userService;
    /**
     * 同步用户发表的帖子数
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void syncUserPostCount(){
        log.info("开始同步用户发的帖子数的数据:ex=>{}", LocalDateTime.now());
        Map<String, Object> userIdAndPostCount = this.redisUtils.hEntries(RedisCacheConstant.USER_POST_COUNT_CACHE_PREFIX);
        List<MiUser> userList = Lists.newCopyOnWriteArrayList();
        for (Map.Entry<String, Object> entry : userIdAndPostCount.entrySet()) {
            String userId = entry.getKey();
            Integer value = Integer.valueOf(String.valueOf(entry.getValue()));
            MiUser user = new MiUser();
            user.setId(Long.valueOf(userId));
            user.setPostCount(value);
            userList.add(user);
        }
        boolean isUpdate = this.userService.updateBatchById(userList);
        if (isUpdate){
            this.redisUtils.del(RedisCacheConstant.USER_POST_COUNT_CACHE_PREFIX);
        }
        log.info("同步用户拥有的帖子数的数据结束:ex=>{}", LocalDateTime.now());
    }
}
