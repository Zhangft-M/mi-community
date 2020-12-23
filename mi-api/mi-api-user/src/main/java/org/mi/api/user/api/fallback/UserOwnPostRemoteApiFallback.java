package org.mi.api.user.api.fallback;

import org.mi.api.user.api.UserOwnPostRemoteApi;
import org.springframework.stereotype.Component;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-23 21:39
 **/
@Component
public class UserOwnPostRemoteApiFallback implements UserOwnPostRemoteApi {
    @Override
    public void addUserOwnPost(Long postId, Integer point) {

    }

    @Override
    public Long getOwnPost(Long postId) {
        return null;
    }
}
