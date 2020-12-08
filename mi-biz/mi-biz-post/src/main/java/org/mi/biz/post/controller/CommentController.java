package org.mi.biz.post.controller;

import lombok.RequiredArgsConstructor;
import org.mi.api.post.entity.Comment;
import org.mi.api.post.vo.CommentTree;
import org.mi.biz.post.service.ICommentService;
import org.mi.common.core.result.R;
import org.mi.security.annotation.Anonymous;
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
        CommentTree commentTree = this.commentService.insertComment(comment);
        return R.success(commentTree);
    }

}
