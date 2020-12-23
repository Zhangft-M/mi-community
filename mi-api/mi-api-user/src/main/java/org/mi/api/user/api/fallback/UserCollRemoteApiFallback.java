package org.mi.api.user.api.fallback;

import org.mi.api.user.api.UserCollRemoteApi;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-23 21:29
 **/
@Component
public class UserCollRemoteApiFallback implements UserCollRemoteApi {
    @Override
    public Set<Long> listUserCollectPostId() {
        return null;
    }
}
