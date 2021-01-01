package org.mi.biz.post.task;

import com.google.common.collect.Lists;
import javafx.geometry.Pos;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mi.api.post.entity.Comment;
import org.mi.api.post.entity.Post;
import org.mi.biz.post.service.ICommentService;
import org.mi.biz.post.service.IPostService;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.constant.UserThumbUpConstant;
import org.mi.common.core.util.RedisUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-31 19:36
 **/
@Slf4j
// @Component
// @EnableScheduling
@RequiredArgsConstructor
public class SyncContentDataTask {

    private final RedisUtils redisUtils;

    private final IPostService postService;

    private final ICommentService commentService;

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(5,10,5000, TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>()
            ,new CustomizableThreadFactory("sync-task-"));

    /**
     * 同步帖子和评论点赞数
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void syncPostData(){
        log.info("----------------------------开始同步Post数据,开始时间为{}------------------------------", LocalDateTime.now());
        Map<String, Object> countMap = this.redisUtils.hEntries(RedisCacheConstant.POST_VIEW_COUNTS_PREFIX);
        List<Post> postList = Lists.newCopyOnWriteArrayList();
        List<String> itemKeys = Lists.newCopyOnWriteArrayList();
        for (Map.Entry<String, Object> entry : countMap.entrySet()) {
            Post post = new Post();
            String contentId = entry.getKey();
            Integer voteUpCount = (Integer) this.redisUtils.hget(UserThumbUpConstant.CONTENT_THUMB_UP_NUM_PREFIX, contentId);
            if (null != voteUpCount) {
                post.setVoteUp(voteUpCount);
            }
            post.setId(Long.valueOf(contentId));
            post.setViewCount(Long.valueOf(String.valueOf(entry.getValue())));
            postList.add(post);
            itemKeys.add(entry.getKey());
        }

        executor.execute(()->{
            this.postService.updateBatchById(postList);
        });
        executor.execute(()->{
            this.redisUtils.hdel(RedisCacheConstant.POST_VIEW_COUNTS_PREFIX,itemKeys.toArray(new String[0]));
        });
        log.info("-----------------------同步点赞完成,同步的时间为{}--------------------------", LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void syncCommentData(){
        log.info("----------------------------开始同步Comment数据,开始时间为{}------------------------------", LocalDateTime.now());
        Map<String, Object> commentMaps = this.redisUtils.hEntries(UserThumbUpConstant.CONTENT_THUMB_UP_NUM_PREFIX);
        List<Comment> comments = Lists.newCopyOnWriteArrayList();
        List<String> itemKeys = Lists.newCopyOnWriteArrayList();
        for (Map.Entry<String, Object> entry : commentMaps.entrySet()) {
            Comment comment = new Comment();
            comment.setId(Long.valueOf(entry.getKey()));
            comment.setVoteUp(Integer.valueOf(String.valueOf(entry.getValue())));
            comments.add(comment);
            itemKeys.add(entry.getKey());
        }
        executor.execute(()->{
            this.commentService.updateBatchById(comments);
        });
        executor.execute(()->{
            this.redisUtils.hdel(UserThumbUpConstant.CONTENT_THUMB_UP_NUM_PREFIX,itemKeys);
        });
        log.info("----------------------------开始同步Comment数据,开始时间为{}------------------------------", LocalDateTime.now());
    }
}
