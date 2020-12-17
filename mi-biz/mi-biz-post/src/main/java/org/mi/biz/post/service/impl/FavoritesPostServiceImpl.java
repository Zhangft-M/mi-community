package org.mi.biz.post.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.mi.api.post.entity.UserPostFavorites;
import org.mi.biz.post.mapper.FavoritesPostMapper;
import org.mi.biz.post.service.IFavoritesPostService;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.util.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-09 15:42
 **/
@Service
@RequiredArgsConstructor
public class FavoritesPostServiceImpl extends ServiceImpl<FavoritesPostMapper, UserPostFavorites> implements IFavoritesPostService {

    private final RedisUtils redisUtils;

    @Override
    public Set<Long> listFavoritesPostId(Long userId) {
        Set<Long> results = Sets.newConcurrentHashSet();
        Set<Object> cacheResults = this.redisUtils.sGet(RedisCacheConstant.USER_POST_FAVORITES + userId);
        if (CollUtil.isNotEmpty(cacheResults)) {
            results.addAll(cacheResults.stream().map(data -> (Long) data).collect(Collectors.toSet()));
        } else {
            List<UserPostFavorites> userPostFavorites = this.baseMapper.selectList(Wrappers.<UserPostFavorites>lambdaQuery()
                    .eq(UserPostFavorites::getUserId, userId)
                    .eq(UserPostFavorites::getHasDelete, false));
            results = userPostFavorites.stream().map(UserPostFavorites::getPostId).collect(Collectors.toSet());
            this.redisUtils.sAdd(RedisCacheConstant.USER_POST_FAVORITES + userId, results);
        }
        return results;
    }

    @Override
    public void addFavoritesPost(Long userId, Long postId, Integer type) {
        if (type == 1) {
            this.redisUtils.sAdd(RedisCacheConstant.USER_POST_FAVORITES + userId, postId);
            return;
        }
        this.redisUtils.setRemove(RedisCacheConstant.USER_POST_FAVORITES + userId, postId);
    }
}
