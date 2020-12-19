package org.mi.api.post.api;

import org.mi.api.post.api.fallback.CommentRemoteApiFallback;
import org.mi.api.post.api.fallback.PostRemoteApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-19 20:44
 **/
@FeignClient(name = "MI-POST-SERVER",contextId = "commentRemoteApi",fallback = CommentRemoteApiFallback.class)
public interface CommentRemoteApi {

    @DeleteMapping("byUserId")
    void deleteCommentByUserId(@RequestParam("userId") Long userId, @RequestHeader("from") String from);
}
