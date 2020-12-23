package org.mi.biz.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mi.api.user.entity.UserThumbUp;

import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-22 18:16
 **/
public interface IUserThumbUpService extends IService<UserThumbUp> {
    /**
     * 点赞操作
     * @param thumbUp /
     */
    void thumbUp(UserThumbUp thumbUp);

    /**
     * 通过用户的id查询
     * @param userId
     * @return
     */
    Set<Long> listByUserId(Long userId);
}
