package org.mi.biz.post.controller;

import lombok.RequiredArgsConstructor;
import org.mi.biz.post.service.IFavoritesPostService;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
import org.mi.security.util.SecurityContextHelper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-09 15:33
 **/
@RestController
@RequestMapping("/post/favorites")
@RequiredArgsConstructor
public class FavoritesPostController {

    private final IFavoritesPostService favoritesPostService;

    @GetMapping("{userId}")
    public R<Set<Long>> listFavoritesPostId(@PathVariable Long userId){
        return R.success(this.favoritesPostService.listFavoritesPostId(userId));
    }

    @PostMapping
    public R<Void> addFavoritesPost(Long postId,Integer type){
        Long userId = SecurityContextHelper.getUserId();
        this.favoritesPostService.addFavoritesPost(userId,postId,type);
        return R.success();
    }
}
