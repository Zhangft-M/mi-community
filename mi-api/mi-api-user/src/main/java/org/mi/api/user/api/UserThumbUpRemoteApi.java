package org.mi.api.user.api;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.mi.api.user.api.fallback.MiUserRemoteApiFallback;
import org.mi.api.user.entity.UserThumbUp;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-23 18:02
 **/
@FeignClient(name = "MI-USER-SERVER",contextId = "userThumbUpRemoteApi",fallback = MiUserRemoteApiFallback.class)
public interface UserThumbUpRemoteApi {
    void saveOrUpdate(UserThumbUp data, LambdaUpdateWrapper<UserThumbUp> set);
}
