package org.mi.api.user.api;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.mi.api.user.api.fallback.MiUserRemoteApiFallback;
import org.mi.api.user.entity.UserOwnPost;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-23 21:38
 **/
@FeignClient(name = "MI-USER-SERVER",contextId = "userOwnPostRemoteApi",fallback = MiUserRemoteApiFallback.class)
public interface UserOwnPostRemoteApi {

    @PostMapping("/user/own/post")
    void addUserOwnPost(@RequestParam Long postId, @RequestParam Integer point);

    @GetMapping("/user/own/post")
    Long getOwnPost(@RequestParam Long postId);
}
