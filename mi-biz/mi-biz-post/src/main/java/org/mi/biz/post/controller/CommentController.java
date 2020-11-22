package org.mi.biz.post.controller;

import lombok.RequiredArgsConstructor;
import org.mi.api.post.vo.CommentTree;
import org.mi.biz.post.service.ICommentService;
import org.mi.common.core.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-19 17:00
 **/
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final ICommentService commentService;

    @GetMapping("{postId}")
    public R<List<CommentTree>> list(@PathVariable Long postId){
        List<CommentTree> commentTree = this.commentService.list(postId);
        return R.success(commentTree);
    }

}
