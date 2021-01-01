package org.mi.biz.user.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mi.api.user.entity.UserOwnPost;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
import org.mi.common.core.util.RedisUtils;
import org.mi.security.annotation.Inner;
import org.mi.security.util.SecurityContextHelper;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-23 18:14
 **/

@RestController
@RequestMapping("/user/own/post")
@RequiredArgsConstructor
public class UserOwnPostController {

    private final RedisUtils redisUtils;

    @GetMapping
    public R<Boolean> getOwnPost(@RequestParam Long postId){
        Long userId = SecurityContextHelper.getUserId();
        Boolean isMember = this.redisUtils.sIsMember(RedisCacheConstant.USER_OWN_POST_ID + userId, String.valueOf(postId));
        if (isMember) {
            return R.success(Boolean.TRUE);
        }
        // 缓存未命中，从数据库中查询
        UserOwnPost userOwnPost = new UserOwnPost();
        userOwnPost.setPostId(postId);
        userOwnPost.setUserId(userId);
        UserOwnPost result = userOwnPost.selectOne(Wrappers.<UserOwnPost>lambdaQuery()
                .eq(UserOwnPost::getUserId,userId)
                .eq(UserOwnPost::getPostId,postId));
        if (result != null) {
            this.redisUtils.sAdd(RedisCacheConstant.USER_OWN_POST_ID + userId,postId);
            return R.success(Boolean.TRUE);
        }
        return R.success(Boolean.FALSE);
    }

    @Inner
    @PostMapping
    public R<Void> addUserOwnPost(@RequestParam Long userId,@RequestParam Long postId, @RequestParam Integer point){
        AssertUtil.notNull(postId,point);
        UserOwnPost userOwnPost = new UserOwnPost();
        userOwnPost.setUserId(userId);
        userOwnPost.setPostId(postId);
        userOwnPost.setUsePoint(point);
        userOwnPost.insert();
        return R.success();
    }

}
