package org.mi.biz.user.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mi.api.post.api.CommentRemoteApi;
import org.mi.api.post.api.PostRemoteApi;
import org.mi.api.post.entity.Comment;
import org.mi.api.post.entity.Post;
import org.mi.api.user.api.UserThumbUpRemoteApi;
import org.mi.api.user.entity.UserThumbUp;
import org.mi.biz.user.service.IUserThumbUpService;
import org.mi.common.core.constant.UserThumbUpConstant;
import org.mi.common.core.util.RedisUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-22 15:13
 **/
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class SyncThumbUpDataTask {

    private final IUserThumbUpService userThumbUpService;

    private final RedisUtils redisUtils;


    /**
     * 同步点赞的用户与评论相连接的表
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void syncThumbUpData(){
        log.info("----------------------------开始同步用户与点赞评论数据,开始时间为{}------------------------------",LocalDateTime.now());
        try {
            List<String> keyList = this.redisUtils.scan(UserThumbUpConstant.USER_THUMB_UP_CONTENT_PREFIX + "*");
            Map<String,List<UserThumbUp>> map = Maps.newConcurrentMap();
            keyList.forEach(key->{
                Set<Object> set = this.redisUtils.sGet(key);
                List<UserThumbUp> thumbUpList = set.stream().map(data -> {
                    UserThumbUp thumbUp = new UserThumbUp();
                    thumbUp.setContentId(Long.valueOf(data.toString()));
                    thumbUp.setUserId(Long.valueOf(key.split(":")[4]));
                    thumbUp.setHasDelete(false);
                    return thumbUp;
                }).collect(Collectors.toList());
                map.put(key,thumbUpList);
            });
            for (String key : map.keySet()) {
                // this.userThumbUpRemoteApi.remove(Wrappers.<ThumbUp>lambdaUpdate().eq(ThumbUp::getUserId,key.split(":")[2]));
                this.userThumbUpService.update(Wrappers.<UserThumbUp>lambdaUpdate()
                        .eq(UserThumbUp::getUserId,key.split(":")[4])
                        .set(UserThumbUp::getHasDelete,true));
                map.get(key).forEach(data->{
                    this.userThumbUpService.saveOrUpdate(data,Wrappers.<UserThumbUp>lambdaUpdate()
                            .eq(UserThumbUp::getUserId,data.getUserId())
                            .eq(UserThumbUp::getContentId,data.getContentId())
                            .set(UserThumbUp::getHasDelete,false));
                });
            }
            log.info("-----------------------同步点赞完成,同步的时间为{}--------------------------", LocalDateTime.now());
            log.info("-----------------------开始删除缓存数据,开始时间为{}-------------------------",LocalDateTime.now());
            for (String key : map.keySet()) {
                this.redisUtils.del(String.valueOf(key));
                map.remove(key);
            }
            log.info("-----------------------删除数据,完成时间为{}-------------------------",LocalDateTime.now());
        }catch (Exception e){
            log.info("同步数据失败,通知管理员ex=>{}",e.toString());
            throw new RuntimeException("同步数据失败");
        }
    }


}
