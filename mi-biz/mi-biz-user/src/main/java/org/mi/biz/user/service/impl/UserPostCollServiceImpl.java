package org.mi.biz.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.mi.api.user.entity.UserPostCollections;
import org.mi.biz.user.mapper.UserPostCollMapper;
import org.mi.biz.user.service.IUserPostCollService;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.util.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-23 17:30
 **/
@Service
@RequiredArgsConstructor
public class UserPostCollServiceImpl extends ServiceImpl<UserPostCollMapper, UserPostCollections> implements IUserPostCollService {
    private final RedisUtils redisUtils;

    @Override
    public Set<Long> listUserCollectPostId(Long userId) {
        Set<Long> results = Sets.newConcurrentHashSet();
        Set<Object> cacheResults = this.redisUtils.sGet(RedisCacheConstant.USER_COLLECT_POST + userId);
        if (CollUtil.isNotEmpty(cacheResults)) {
            results.addAll(cacheResults.stream().map(data -> (Long) data).collect(Collectors.toSet()));
        } else {
            List<UserPostCollections> userPostFavorites = this.baseMapper.selectList(Wrappers.<UserPostCollections>lambdaQuery()
                    .eq(UserPostCollections::getUserId, userId)
                    .eq(UserPostCollections::getHasDelete, false));
            if (CollUtil.isNotEmpty(userPostFavorites)) {
                userPostFavorites.forEach(data->{
                    this.redisUtils.sAdd(RedisCacheConstant.USER_COLLECT_POST + userId,data.getPostId());
                });
            }
        }
        return results;
    }

    @Override
    public void addCollectPost(Long userId, Long postId, Integer type) {
        if (type == 1) {
            this.redisUtils.sAdd(RedisCacheConstant.USER_COLLECT_POST + userId, postId);
            return;
        }
        this.redisUtils.setRemove(RedisCacheConstant.USER_COLLECT_POST + userId, postId);
    }
}
