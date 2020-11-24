package org.mi.biz.post.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mi.api.post.entity.Comment;
import org.mi.api.post.entity.Post;
import org.mi.api.post.entity.ThumbUp;
import org.mi.biz.post.mapper.CommentMapper;
import org.mi.biz.post.service.ICommentService;
import org.mi.biz.post.service.IPostService;
import org.mi.biz.post.service.IThumbUpService;
import org.mi.common.core.constant.ThumbUpConstant;
import org.mi.common.core.util.RedisUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Wrapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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

    private final ICommentService commentService;

    private final IPostService postService;

    private final IThumbUpService thumbUpService;

    private final RedisUtils redisUtils;

    /**
     * 同步点赞的用户与评论相连接的表
     */
    @Scheduled(cron = "* * 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void syncVoteUpData(){
      log.info("----------------------------开始同步点赞数据,开始时间为{}------------------------------",LocalDateTime.now());
      try {
          List<String> keyList = this.redisUtils.scan(ThumbUpConstant.CONTENT_THUMB_UP_NUM_PREFIX + "*");
          List<Comment> collect = keyList.stream().map(key -> Comment.builder().id(Long.valueOf(key.split(":")[2]))
                  .voteUp((Integer) this.redisUtils.get(key))
                  .build()).collect(Collectors.toList());
          List<Post> postList = collect.stream().map(data -> {
              Post post = new Post();
              post.setId(data.getId());
              post.setVoteUp(data.getVoteUp());
              return post;
          }).collect(Collectors.toList());
          this.commentService.updateBatchById(collect);
          this.postService.updateBatchById(postList);
          this.redisUtils.del(keyList.toArray(new String[0]));
          log.info("-----------------------同步点赞完成,同步的时间为{}--------------------------", LocalDateTime.now());
      }catch (Exception e){
        log.info("同步数据失败,通知管理员");
        throw new RuntimeException("同步数据失败");
      }
    }

    /**
     * 同步点赞的用户与评论相连接的表
     */
    @Scheduled(cron = "* * 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void syncThumbUpData(){
        log.info("----------------------------开始同步用户与点赞评论数据,开始时间为{}------------------------------",LocalDateTime.now());
        try {
            List<String> keyList = this.redisUtils.scan(ThumbUpConstant.USER_CONTENT_PREFIX + "*");
            Map<String,List<ThumbUp>> map = new HashMap<>();
            keyList.forEach(key->{
                Set<Object> set = this.redisUtils.sGet(key);
                List<ThumbUp> thumbUpList = set.stream().map(data -> {
                    ThumbUp thumbUp = new ThumbUp();
                    thumbUp.setContentId((Long) data);
                    thumbUp.setUserId(Long.valueOf(key.split(":")[2]));
                    thumbUp.setHasDelete(false);
                    return thumbUp;
                }).collect(Collectors.toList());
                map.put(key,thumbUpList);
            });
            for (String key : map.keySet()) {
                // this.thumbUpService.remove(Wrappers.<ThumbUp>lambdaUpdate().eq(ThumbUp::getUserId,key.split(":")[2]));
                this.thumbUpService.update(Wrappers.<ThumbUp>lambdaUpdate()
                        .eq(ThumbUp::getUserId,key.split(":")[2])
                        .set(ThumbUp::getHasDelete,true));
                map.get(key).forEach(data->{
                    this.thumbUpService.saveOrUpdate(data,Wrappers.<ThumbUp>lambdaUpdate()
                            .eq(ThumbUp::getUserId,data.getUserId())
                            .eq(ThumbUp::getContentId,data.getContentId())
                            .set(ThumbUp::getHasDelete,false));
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
            log.info("同步数据失败,通知管理员");
            throw new RuntimeException("同步数据失败");
        }
    }


}
