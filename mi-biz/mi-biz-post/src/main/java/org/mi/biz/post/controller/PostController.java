package org.mi.biz.post.controller;

import lombok.RequiredArgsConstructor;
import org.common.mp.component.PageParam;
import org.mi.api.post.entity.PostEntity;
import org.mi.api.post.query.PostQueryCriteria;
import org.mi.biz.post.service.IPostService;
import org.mi.common.core.result.PageResult;
import org.mi.common.core.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 17:51
 **/
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final IPostService postService;

    @GetMapping
    public R<PageResult> list(PostQueryCriteria criteria, PageParam pageParam){
        PageResult result = this.postService.list(criteria,pageParam);
        return R.success(result);
    }

    @PostMapping
    public R<Void> save(@RequestBody PostEntity postEntity){
        this.postService.savePost(postEntity);
        return R.success();
    }

    @PutMapping
    public R<Void> update(@RequestBody PostEntity postEntity){
        this.postService.updatePost(postEntity);
        return R.success();
    }

    @DeleteMapping
    public R<Void> delete(Set<Long> ids){
        this.postService.deletePost(ids);
        return R.success();
    }
}
