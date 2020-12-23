package org.mi.api.post.api.fallback;

import org.mi.api.post.api.CommentRemoteApi;
import org.mi.api.post.entity.Comment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-19 20:45
 **/
@Component
public class CommentRemoteApiFallback implements CommentRemoteApi {
    @Override
    public void deleteCommentByUserId(Long userId, String from) {

    }

    @Override
    public void updateBatchById(List<Comment> collect) {

    }
}
