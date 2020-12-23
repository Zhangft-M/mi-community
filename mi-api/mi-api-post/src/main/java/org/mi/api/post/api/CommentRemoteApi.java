package org.mi.api.post.api;

import org.mi.api.post.api.fallback.CommentRemoteApiFallback;
import org.mi.api.post.api.fallback.PostRemoteApiFallback;
import org.mi.api.post.entity.Comment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-19 20:44
 **/
@FeignClient(name = "MI-POST-SERVER",contextId = "commentRemoteApi",fallback = CommentRemoteApiFallback.class)
public interface CommentRemoteApi {

    /**
     * 通过用户名删除
     * @param userId
     * @param from
     */
    @DeleteMapping("/comment/byUserId")
    void deleteCommentByUserId(@RequestParam("userId") Long userId, @RequestHeader("from") String from);

    /**
     * 批量更新
     * @param collect
     */
    @PutMapping("/comment/update/batch")
    void updateBatchById(@RequestParam List<Comment> collect);
}
