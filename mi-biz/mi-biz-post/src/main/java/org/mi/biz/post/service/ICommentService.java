package org.mi.biz.post.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mi.api.post.entity.Comment;
import org.mi.api.post.vo.CommentTree;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-19 17:45
 **/

public interface ICommentService extends IService<Comment> {

    /**
     * 根据postId查询评论
     * @param postId /
     * @return 树形结构数据
     */
    List<CommentTree> list(Long postId);

    /**
     * 插入一条数据
     * @param comment /
     * @return /
     */
    CommentTree insertComment(Comment comment);

    /**
     * 根据用户的id删除评论,删除当前级以及下级的所有回复评论
     * @param userId
     * @param commentId
     * @param postId
     */
    void deleteComment(Long userId, Long commentId, Long postId);

    /**
     * 通过postId删除评论
     * @param postId /
     */
    void deleteCommentByPostId(Long postId);

    /**
     * 通过用户的id来删除评论
     * @param userId
     */
    void deleteCommentByUserId(Long userId);
}
