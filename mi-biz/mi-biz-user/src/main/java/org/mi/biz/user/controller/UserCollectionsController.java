package org.mi.biz.user.controller;

import lombok.RequiredArgsConstructor;
import org.mi.biz.user.service.IUserPostCollService;
import org.mi.common.core.result.R;
import org.mi.security.util.SecurityContextHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-23 17:22
 **/
@RestController
@RequestMapping("/user/collections")
@RequiredArgsConstructor
public class UserCollectionsController {

    private final IUserPostCollService userPostCollService;

    @GetMapping("/post/ids")
    public R<Set<Long>> listUserCollectPostId(){
        Long userId = SecurityContextHelper.getUserId();
        return R.success(this.userPostCollService.listUserCollectPostId(userId));
    }

    @PostMapping("/post/add")
    public R<Void> addCollectPost(Long postId,Integer type){
        Long userId = SecurityContextHelper.getUserId();
        this.userPostCollService.addCollectPost(userId,postId,type);
        return R.success();
    }
}
