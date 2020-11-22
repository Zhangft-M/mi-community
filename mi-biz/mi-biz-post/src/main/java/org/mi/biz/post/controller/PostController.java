package org.mi.biz.post.controller;

import lombok.RequiredArgsConstructor;
import org.mi.api.post.entity.EsPost;
import org.mi.api.post.entity.Post;
import org.mi.api.post.query.PostQueryCriteria;
import org.mi.api.post.vo.PostVO;
import org.mi.biz.post.service.IPostService;
import org.mi.common.core.result.PageResult;
import org.mi.common.core.result.R;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
@CrossOrigin
public class PostController {

    private final IPostService postService;

    @GetMapping
    public R<PageResult> list(PostQueryCriteria criteria, Pageable pageParam){
        PageResult result = this.postService.list(criteria,pageParam);
        return R.success(result);
    }

    @GetMapping("{id}")
    public R<EsPost> getById(@PathVariable() Long id){
        EsPost result = this.postService.getDataById(id);
        return R.success(result);
    }

    @GetMapping("recommend")
    public R<List<PostVO>> listRecommend(){
        List<PostVO> result = this.postService.listRecommend();
        return R.success(result);
    }

    @PostMapping
    public R<Void> save(@RequestBody Post postEntity){
        this.postService.savePost(postEntity);
        return R.success();
    }

    @PutMapping
    public R<Void> update(@RequestBody Post postEntity){
        this.postService.updatePost(postEntity);
        return R.success();
    }

    @DeleteMapping
    public R<Void> delete(Set<Long> ids){
        this.postService.deletePost(ids);
        return R.success();
    }
}
