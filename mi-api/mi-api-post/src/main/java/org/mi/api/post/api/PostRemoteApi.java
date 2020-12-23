package org.mi.api.post.api;

import org.mi.api.post.api.fallback.PostRemoteApiFallback;
import org.mi.api.post.entity.Post;
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
 * @create: 2020-12-19 20:39
 **/
@FeignClient(name = "MI-POST-SERVER",contextId = "postRemoteApi",fallback = PostRemoteApiFallback.class)
public interface PostRemoteApi {

    @DeleteMapping("/post/byUserId")
    void deleteByUserId(@RequestParam("userId") Long userId, @RequestHeader("from") String from);

    @PutMapping("/post/update/batch")
    void updateBatchById(@RequestParam List<Post> postList);
}
