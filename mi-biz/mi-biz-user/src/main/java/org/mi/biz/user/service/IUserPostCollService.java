package org.mi.biz.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.mi.api.user.entity.UserPostCollections;

import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-09 15:39
 **/
public interface IUserPostCollService extends IService<UserPostCollections> {
    /**
     * 查询用户的收藏帖子的id
     * @param userId
     * @return
     */
    Set<Long> listUserCollectPostId(Long userId);

    /**
     * 添加或删除收藏
     * @param userId
     * @param postId
     * @param type
     */
    void addCollectPost(Long userId, Long postId, Integer type);
}
