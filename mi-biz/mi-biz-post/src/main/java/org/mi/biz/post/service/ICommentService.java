package org.mi.biz.post.service;

import org.mi.api.post.vo.CommentTree;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-19 17:45
 **/

public interface ICommentService {

    /**
     * 根据postId查询评论
     * @param postId /
     * @return 树形结构数据
     */
    List<CommentTree> list(Long postId);
}
