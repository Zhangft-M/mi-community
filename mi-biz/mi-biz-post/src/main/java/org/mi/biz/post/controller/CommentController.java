package org.mi.biz.post.controller;

import lombok.RequiredArgsConstructor;
import org.mi.api.post.entity.Comment;
import org.mi.api.post.vo.CommentTree;
import org.mi.biz.post.service.ICommentService;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
import org.mi.security.annotation.Anonymous;
import org.mi.security.annotation.Inner;
import org.mi.security.util.SecurityContextHelper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-19 17:00
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final ICommentService commentService;

    @Anonymous
    @GetMapping("{postId}")
    public R<List<CommentTree>> list(@PathVariable Long postId){
        List<CommentTree> commentTree = this.commentService.list(postId);
        return R.success(commentTree);
    }

    @PostMapping
    public R<CommentTree> insertComment(@RequestBody Comment comment){
        AssertUtil.idsIsNull(comment.getId(),comment.getUserId());
        Long userId = SecurityContextHelper.getUserId();
        comment.setUserId(userId);
        CommentTree commentTree = this.commentService.insertComment(comment);
        return R.success(commentTree);
    }

    @DeleteMapping
    public R<Void> deleteComment(@RequestParam("postId") Long postId,@RequestParam("commentId") Long commentId){
        AssertUtil.idsIsNotNull(postId,commentId);
        Long userId = SecurityContextHelper.getUserId();
        this.commentService.deleteComment(userId,commentId,postId);
        return R.success();
    }

    @DeleteMapping("byPostId")
    public R<Void> deleteComment(@RequestParam("postId") Long postId){
        AssertUtil.idIsNotNull(postId);
        this.commentService.deleteCommentByPostId(postId);
        return R.success();
    }

    @Inner
    @DeleteMapping("byUserId")
    public R<Void> deleteCommentByUserId(Long userId){
        AssertUtil.idIsNotNull(userId);
        this.commentService.deleteCommentByUserId(userId);
        return R.success();
    }

    @Inner
    @PutMapping("/update/batch")
    public R<Void> updateBatchById(List<Comment> comments){
        this.commentService.updateBatchById(comments);
        return R.success();
    }

}
