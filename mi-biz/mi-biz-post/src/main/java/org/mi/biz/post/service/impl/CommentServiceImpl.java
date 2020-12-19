package org.mi.biz.post.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.mi.api.post.entity.Comment;
import org.mi.api.post.entity.EsComment;
import org.mi.api.post.entity.Post;
import org.mi.api.post.mapstruct.EsCommentMapStruct;
import org.mi.api.post.vo.CommentTree;
import org.mi.api.tool.api.ContentCheckRemoteApi;
import org.mi.api.tool.dto.EmailDTO;
import org.mi.api.tool.entity.Checker;
import org.mi.api.user.api.MiUserRemoteApi;
import org.mi.api.user.dto.MiUserDTO;
import org.mi.biz.post.mapper.CommentMapper;
import org.mi.biz.post.service.ICommentService;
import org.mi.biz.post.service.IPostService;
import org.mi.common.core.constant.EmailConstant;
import org.mi.common.core.constant.ThumbUpConstant;
import org.mi.common.core.exception.IllegalOperationException;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.SmsSendFailException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-19 18:33
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    private final ContentCheckRemoteApi contentCheckRemoteApi;

    private IPostService postService;

    private final MiUserRemoteApi miUserRemoteApi;

    private final RocketMQTemplate rocketMQTemplate;

    private final RedisUtils redisUtils;

    private final EsCommentMapStruct commentMapStruct;

    private List<CommentTree> secondFloorData;

    @Autowired
    public void setPostService(IPostService postService) {
        this.postService = postService;
    }

    @Override
    public List<CommentTree> list(Long postId) {
        // 构建查询条件
        Query query = this.createQueryCriteria(postId);
        SearchHits<EsComment> result = this.elasticsearchRestTemplate.search(query, EsComment.class);
        List<CommentTree> commentList = result.stream().map(
                data -> {
                    Integer voteUpCount = (Integer) this.redisUtils.get(ThumbUpConstant.CONTENT_THUMB_UP_NUM_PREFIX + data.getContent().getId());
                    if (!Objects.isNull(voteUpCount)) {
                        // 命中缓存，更新一下点赞数
                        data.getContent().setVoteUp(voteUpCount);
                    } else {
                        this.redisUtils.set(ThumbUpConstant.CONTENT_THUMB_UP_NUM_PREFIX + data.getContent().getId(), data.getContent().getVoteUp());
                    }
                    return this.commentMapStruct.toDto(data.getContent());
                }).sorted(Comparator.comparingInt(CommentTree::getVoteUp).reversed()).collect(Collectors.toList());
        List<CommentTree> commentTree = this.createCommentTree(commentList);
        commentTree.forEach(data -> {
            secondFloorData = Lists.newCopyOnWriteArrayList();
            this.getSecondFloorTree(data, data.getId());
            if (CollUtil.isNotEmpty(secondFloorData)) {
                data.getChildren().clear();
                data.setChildren(secondFloorData);
            }
        });
        return commentTree;
    }

    @Override
    public CommentTree insertComment(Comment comment) {
        Comment parentComment = null;
        if (comment.getParentId() != null){
            parentComment = this.baseMapper.selectById(comment.getParentId());
            if (null == parentComment) {
                throw new IllegalArgumentException("请求参数不对");
            }
        }else {
            comment.setParentId(0L);
        }
        String content = comment.getContent();
        Checker checker = this.contentCheckRemoteApi.checkTxt(content).getData();
        AssertUtil.statusIsTrue(checker.getStatus(), "内容涉嫌违规");
        if (comment.insert()) {
            // 审核通过的，直接从索引库查找并返回数据
            EsComment esComment = this.elasticsearchRestTemplate.get(String.valueOf(comment.getId()), EsComment.class);
            // 判断用户是否接收回复邮件提醒,如果接收则发送邮件,否侧不发
            if (null != esComment) {
                // 发送帖子回复
                this.sendPostReplyEmail(esComment);
                // 发送评论回复
                this.senCommentReplyEmail(esComment,parentComment);
            }

            return Optional.ofNullable(this.commentMapStruct.toDto(esComment)).orElseGet(CommentTree::new);
        }
        return null;
    }

    /**
     * 发送评论回复
     *
     * @param esComment
     * @param parentComment
     */
    private void senCommentReplyEmail(EsComment esComment, Comment parentComment) {
        if (!esComment.getReceiveReply()) {
            return;
        }
        if (esComment.getParentId() == 0){
            return;
        }
        if (null == parentComment) {
            return;
        }
        // 远程查找用户的信息
        MiUserDTO fromUser = getMiUserDTO(esComment.getUserId());
        MiUserDTO toUser = getMiUserDTO(parentComment.getUserId());
        if (null == fromUser || null == toUser) {
            throw new RuntimeException("查找用户信息失败");
        }
        // 构建并发送邮件
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTitle(parentComment.getContent());
        emailDTO.setContent(esComment.getContent());
        emailDTO.setTo(toUser.getEmail());
        emailDTO.setReplyNickName(fromUser.getNickName());
        this.rocketMQTemplate.asyncSend(EmailConstant.REPLY_COMMENT_DESTINATION, emailDTO, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("发送成功");
            }

            @Override
            public void onException(Throwable e) {
                log.error("发送失败");
                throw new SmsSendFailException("邮件发送失败");
            }
        });
    }

    /**
     * 发送帖子回复邮件
     *
     * @param comment /
     */
    private void sendPostReplyEmail(EsComment comment) {
        // 通过postId查找用户的信息
        if (comment.getParentId() != 0){
            return;
        }
        Post post = this.postService.getById(comment.getPostId());
        if (null == post) {
            return;
        }
        if (!post.getReceiveReply()) {
            return;
        }
        // 调用微服务查找用户的信息
        MiUserDTO fromUser = getMiUserDTO(comment.getUserId());
        MiUserDTO toUser = getMiUserDTO(post.getUserId());
        if (null == fromUser || toUser == null) {
            throw new RuntimeException("查找用户信息失败");
        }
        // 发送邮件
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTitle(post.getTitle());
        emailDTO.setReplyNickName(fromUser.getNickName());
        emailDTO.setTo(toUser.getEmail());
        emailDTO.setContent(comment.getContent());
        this.rocketMQTemplate.asyncSend(EmailConstant.REPLY_POST_DESTINATION, emailDTO, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("发送成功");
            }

            @Override
            public void onException(Throwable e) {
                log.error("发送失败,保存日志,后续补偿");
                throw new SmsSendFailException(e.getMessage());
            }
        });
    }

    private MiUserDTO getMiUserDTO(Long userId) {
        return this.miUserRemoteApi.getUserInfo(userId);
    }

    /**
     * 将多层树形结构转化成两层
     *
     * @param data
     * @param id
     */
    private void getSecondFloorTree(CommentTree data, Long id) {
        if (data.getChildren() == null) {
            secondFloorData.add(data);
            return;
        }
        if (!data.getId().equals(id)) {
            CommentTree tree = new CommentTree();
            tree.setParentNickName(data.getParentNickName());
            tree.setHasAdoption(data.getHasAdoption());
            tree.setContent(data.getContent());
            tree.setId(data.getId());
            tree.setUserId(data.getUserId());
            tree.setVoteUp(data.getVoteUp());
            tree.setParentId(data.getParentId());
            tree.setUpdateTime(data.getUpdateTime());
            tree.setUserAvatar(data.getUserAvatar());
            tree.setUserNickName(data.getUserNickName());
            tree.setVoteDown(data.getVoteDown());
            secondFloorData.add(tree);
        }
        data.getChildren().forEach(child -> {
            this.getSecondFloorTree(child, id);
        });
    }

    /**
     * 构建树形，渲染两级数据
     *
     * @param commentList
     * @return
     */
    private List<CommentTree> createCommentTree(List<CommentTree> commentList) {
        Map<String, List<CommentTree>> map = new HashMap<>();
        List<CommentTree> trees = new ArrayList<>();
        for (CommentTree commentTree : commentList) {
            if (commentTree.getParentId().equals(0L)) {
                trees.add(commentTree);
            }
            for (CommentTree node : commentList) {
                if (node.getParentId().equals(commentTree.getId())) {
                    if (null == commentTree.getChildren()) {
                        commentTree.setChildren(new ArrayList<>());
                    }
                    node.setParentNickName(commentTree.getUserNickName());
                    commentTree.add(node);
                }
            }
        }
        commentList.clear();
        return trees;
    }


    /**
     * 初始化查询条件
     *
     * @param postId
     * @return
     */
    private Query createQueryCriteria(Long postId) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.must(QueryBuilders.termQuery("post_id", postId))
                .filter(QueryBuilders.termQuery("status", true))
                .filter(QueryBuilders.termQuery("has_delete", false));
        searchQueryBuilder.withQuery(queryBuilder);
        return searchQueryBuilder.build();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long userId, Long commentId, Long postId) {
        Comment comment = this.baseMapper.selectById(commentId);
        Post post = this.postService.getById(postId);
        List<Long> deleteCommentIds = Lists.newCopyOnWriteArrayList();
        if (null != comment){
            if (comment.getUserId().equals(userId)){
                // 递归遍历获得所有需要删除的id(包括当前节点的评论id和当前节点所有子节点的id)
                this.listDeleteIds(commentId,deleteCommentIds);
            }
        }else if (null != post) {
            if (post.getUserId().equals(userId)) {
                this.listDeleteIds(commentId,deleteCommentIds);
            }
        }else {
            throw new IllegalOperationException("没有权限进行该项操作");
        }
        this.removeByIds(deleteCommentIds);
    }

    /**
     *
     * @param commentId 当前节点的id
     * @param deleteCommentIds 需要删除的节点的集合
     */
    private void listDeleteIds(Long commentId, List<Long> deleteCommentIds) {
        deleteCommentIds.add(commentId);
        List<Comment> comments = this.baseMapper.selectList(Wrappers.<Comment>lambdaQuery().eq(Comment::getParentId, commentId));
        if (CollUtil.isEmpty(comments)){
            return;
        }
        comments.forEach(comment -> this.listDeleteIds(comment.getId(),deleteCommentIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommentByPostId(Long postId) {
        this.baseMapper.delete(Wrappers.<Comment>lambdaUpdate().eq(Comment::getPostId,postId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommentByUserId(Long userId) {
        List<Comment> comments = this.baseMapper.selectList(Wrappers.<Comment>lambdaQuery().eq(Comment::getUserId, userId));
        if (CollUtil.isEmpty(comments)) {
            return;
        }
        List<Long> deleteCommentIds = Lists.newCopyOnWriteArrayList();
        comments.forEach(comment -> {
            this.listDeleteIds(comment.getId(),deleteCommentIds);
        });
        this.removeByIds(deleteCommentIds);
    }
}
