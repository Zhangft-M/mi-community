package org.mi.api.post.api.fallback;

import lombok.extern.slf4j.Slf4j;
import org.mi.api.post.api.PostRemoteApi;
import org.springframework.stereotype.Component;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-19 20:39
 **/
@Slf4j
@Component
public class PostRemoteApiFallback implements PostRemoteApi {
    @Override
    public void deleteByUserId(Long userId, String from) {
        log.warn("调用根据用户id删除帖子的接口失败");
        throw new RuntimeException("调用根据用户id删除帖子的接口失败");
    }
}
