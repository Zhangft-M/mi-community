package org.mi.biz.post.controller;

import lombok.RequiredArgsConstructor;
import org.mi.api.post.dto.EsPostDTO;
import org.mi.api.post.entity.EsPost;
import org.mi.api.post.entity.Post;
import org.mi.api.post.query.PostQueryCriteria;
import org.mi.api.post.vo.PostVO;
import org.mi.biz.post.service.IPostService;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.PageResult;
import org.mi.common.core.result.R;
import org.mi.security.annotation.Anonymous;
import org.mi.security.util.SecurityContextHelper;
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
public class PostController {

    private final IPostService postService;

    @Anonymous
    @GetMapping
    public R<PageResult> list(PostQueryCriteria criteria, Pageable pageParam){
        PageResult result = this.postService.list(criteria,pageParam);
        return R.success(result);
    }

    @Anonymous
    @GetMapping("{id}")
    public R<EsPost> getById(@PathVariable Long id){
        EsPost result = this.postService.getDataById(id);
        return R.success(result);
    }

    @Anonymous
    @GetMapping("recommend")
    public R<List<PostVO>> listRecommend(){
        List<PostVO> result = this.postService.listRecommend();
        return R.success(result);
    }

    @PostMapping
    public R<Void> save(@RequestBody Post postEntity){
        AssertUtil.idsIsNull(postEntity.getId(), postEntity.getUserId());
        setUserId(postEntity);
        this.postService.savePost(postEntity);
        return R.success();
    }

    private void setUserId(Post postEntity) {
        Long userId = SecurityContextHelper.getUserId();
        postEntity.setUserId(userId);
    }

    @PutMapping
    public R<Void> update(@RequestBody Post postEntity){
        AssertUtil.idIsNotNull(postEntity.getId());
        this.setUserId(postEntity);
        this.postService.updatePost(postEntity);
        return R.success();
    }

    @DeleteMapping
    public R<Void> delete(Set<Long> ids){
        this.postService.deletePost(ids);
        return R.success();
    }

    @GetMapping("userId/{userId}")
    public R<List<EsPostDTO>> listByUserId(@PathVariable Long userId){
        List<EsPostDTO> esPostDTOS = this.postService.listByUserId(userId);
        return R.success(esPostDTOS);
    }

    @GetMapping("favorites/{userId}")
    public R<List<EsPostDTO>> listUserFavorites(@PathVariable Long userId){
        List<EsPostDTO> esPostDTOS = this.postService.listUserFavorites(userId);
        return R.success(esPostDTOS);
    }
}
