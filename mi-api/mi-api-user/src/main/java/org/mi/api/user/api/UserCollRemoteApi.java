package org.mi.api.user.api;

import org.mi.api.user.api.fallback.MiUserRemoteApiFallback;
import org.mi.api.user.api.fallback.UserCollRemoteApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-23 21:28
 **/
@FeignClient(name = "MI-USER-SERVER",contextId = "userCollRemoteApi",fallback = UserCollRemoteApiFallback.class)
public interface UserCollRemoteApi {
    @GetMapping("/user/collections/post/ids")
    Set<Long> listUserCollectPostId();
}
