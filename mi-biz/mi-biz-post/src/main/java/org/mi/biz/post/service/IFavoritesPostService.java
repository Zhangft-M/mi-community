package org.mi.biz.post.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mi.api.post.entity.UserPostFavorites;

import java.util.List;
import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-09 15:39
 **/
public interface IFavoritesPostService extends IService<UserPostFavorites> {
    Set<Long> listFavoritesPostId(Long userId);

    /**
     * 添加或删除收藏
     * @param userId
     * @param postId
     * @param type
     */
    void addFavoritesPost(Long userId, Long postId, Integer type);
}
