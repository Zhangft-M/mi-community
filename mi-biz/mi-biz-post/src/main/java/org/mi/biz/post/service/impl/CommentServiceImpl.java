package org.mi.biz.post.service.impl;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.mi.api.post.entity.EsComment;
import org.mi.api.post.mapstruct.EsCommentMapStruct;
import org.mi.api.post.vo.CommentTree;
import org.mi.biz.post.service.ICommentService;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
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
public class CommentServiceImpl implements ICommentService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    private final EsCommentMapStruct commentMapStruct;

    private List<CommentTree> secondFloorData;

    @Override
    public List<CommentTree> list(Long postId) {
        // 构建查询条件
        Query query = this.createQueryCriteria(postId);
        SearchHits<EsComment> result = this.elasticsearchRestTemplate.search(query, EsComment.class);
        List<CommentTree> commentList = result.stream().map(data-> this.commentMapStruct.toDto(data.getContent()))
                .sorted(Comparator.comparingInt(CommentTree::getVoteUp).reversed()).collect(Collectors.toList());
        List<CommentTree> commentTree = this.createCommentTree(commentList);
        /*commentTree.forEach(data->{
            secondFloorData = new ArrayList<>();
            this.getSecondFloorTree(data,data.getId());
            if (CollUtil.isNotEmpty(secondFloorData)){
                data.getChildren().clear();
                data.setChildren(secondFloorData);
            }
        });*/
        return commentTree;
    }

    private void getSecondFloorTree(CommentTree data, Long id) {
        if (data.getChildren() == null){
            secondFloorData.add(data);
            return;
        }
        if (!data.getId().equals(id)){
            CommentTree tree = new CommentTree();
            tree.setParentName(data.getParentName());
            tree.setHasAdoption(data.getHasAdoption());
            tree.setContent(data.getContent());
            tree.setId(data.getId());
            tree.setParentId(data.getParentId());
            tree.setUpdateTime(data.getUpdateTime());
            tree.setUserAvatar(data.getUserAvatar());
            tree.setUsername(data.getUsername());
            tree.setVoteDown(data.getVoteDown());
            secondFloorData.add(tree);
        }
        data.getChildren().forEach(child->{
            this.getSecondFloorTree(child, id);
        });
    }

    /**
     * 构建树形，渲染两级数据
     * @param commentList
     * @return
     */
    private List<CommentTree> createCommentTree(List<CommentTree> commentList) {
        Map<String,List<CommentTree>> map = new HashMap<>();
        List<CommentTree> trees = new ArrayList<>();
        for (CommentTree commentTree : commentList) {
            if (commentTree.getParentId().equals(0L)){
                trees.add(commentTree);
            }
            for (CommentTree node : commentList) {
                if (node.getParentId().equals(commentTree.getId())){
                    if (null == commentTree.getChildren()){
                        commentTree.setChildren(new ArrayList<>());
                    }
                    node.setParentName(commentTree.getUsername());
                    commentTree.add(node);
                }
            }
        }
        commentList.clear();
        return trees;
    }



    /**
     * 初始化查询条件
     * @param postId
     * @return
     */
    private Query createQueryCriteria(Long postId) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.must(QueryBuilders.termQuery("post_id",postId))
                .filter(QueryBuilders.termQuery("status",true))
                .filter(QueryBuilders.termQuery("has_delete",false));
        searchQueryBuilder.withQuery(queryBuilder);
        return searchQueryBuilder.build();
    }
}
