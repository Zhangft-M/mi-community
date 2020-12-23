package org.mi.api.user.api.fallback;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.mi.api.user.api.UserThumbUpRemoteApi;
import org.mi.api.user.entity.UserThumbUp;
import org.springframework.stereotype.Component;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-23 18:03
 **/
@Component
public class UserThumbUpRemoteApiFallback implements UserThumbUpRemoteApi {
    @Override
    public void saveOrUpdate(UserThumbUp data, LambdaUpdateWrapper<UserThumbUp> set) {

    }
}
