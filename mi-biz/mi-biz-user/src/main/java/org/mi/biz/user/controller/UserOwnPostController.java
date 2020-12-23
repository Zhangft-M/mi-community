package org.mi.biz.user.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mi.api.user.entity.UserOwnPost;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
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

    @GetMapping
    public R<Long> getOwnPost(@RequestParam Long postId){
        Long userId = SecurityContextHelper.getUserId();
        UserOwnPost userOwnPost = new UserOwnPost();
        userOwnPost.setPostId(postId);
        userOwnPost.setUserId(userId);
        UserOwnPost result = userOwnPost.selectOne(Wrappers.lambdaQuery(userOwnPost));
        return R.success(Optional.ofNullable(result.getPostId()).orElse(null));
    }

    @Inner
    @PostMapping
    public R<Void> addUserOwnPost(@RequestParam Long postId, @RequestParam Integer point){
        AssertUtil.notNull(postId,point);
        UserOwnPost userOwnPost = new UserOwnPost();
        userOwnPost.setUserId(SecurityContextHelper.getUserId());
        userOwnPost.setPostId(postId);
        userOwnPost.setUsePoint(point);
        userOwnPost.insert();
        return R.success();

    }

}
