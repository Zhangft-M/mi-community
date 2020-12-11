package org.mi.biz.post.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.common.mp.annotation.Query;
import org.common.mp.util.QueryUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.mi.api.post.dto.EsPostDTO;
import org.mi.api.post.entity.EsPost;
import org.mi.api.post.entity.Post;
import org.mi.api.post.mapstruct.EsPostMapStruct;
import org.mi.api.post.query.PostQueryCriteria;
import org.mi.api.post.vo.PostVO;
import org.mi.api.tool.api.ContentCheckRemoteApi;
import org.mi.api.tool.entity.Checker;
import org.mi.biz.post.mapper.PostMapper;
import org.mi.biz.post.service.IFavoritesPostService;
import org.mi.biz.post.service.IPostService;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.ThumbUpConstant;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.PageResult;
import org.mi.common.core.util.RedisUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 18:04
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements IPostService {

    private final EsPostMapStruct postMapStruct;

    private final IFavoritesPostService favoritesPostService;

    private final ContentCheckRemoteApi contentCheckRemoteApi;

    private final RedisUtils redisUtils;

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    private static final Boolean IS_RECOMMEND = true;

    @Override
    public EsPost getDataById(Long id) {
        AssertUtil.idIsNotNull(id);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        SearchHit<EsPost> one = this.elasticsearchRestTemplate.searchOne(builder.withQuery(QueryBuilders.termQuery("_id", id)).build(), EsPost.class);
        AssertUtil.notNull(one);
        // 查询缓存，记录是否有点赞记录
        Integer count = (Integer) this.redisUtils.get(ThumbUpConstant.CONTENT_THUMB_UP_NUM_PREFIX + id);
        if (!Objects.isNull(count)) {
            // 更新数据
            one.getContent().setVoteUp(count);
        } else {
            // 保存一份缓存
            this.redisUtils.set(ThumbUpConstant.CONTENT_THUMB_UP_NUM_PREFIX + id, one.getContent().getVoteUp());
        }
        return one.getContent();
    }

    @Override
    public PageResult list(PostQueryCriteria criteria, Pageable pageParam) {

        // Page<EsPost> pageResult = this.postDAO.findByCategoryIdOrTitleOrUsername(criteria.getId(), criteria.getTitle(), criteria.getUsername(), pageRequest);
        NativeSearchQuery query = this.createQueryCriteria(criteria, pageParam);
        SearchHits<EsPost> searchResults = this.elasticsearchRestTemplate.search(query, EsPost.class);
        List<EsPost> postEntityList = searchResults.stream().map(SearchHit::getContent).collect(Collectors.toList());
        long total = searchResults.getTotalHits();
        return PageResult.of(total, postEntityList);
        // return PageResult.of(pageResult.getTotalElements(), pageResult.getContent());
    }

    /**
     * 创建查询条件对象
     *
     * @param criteria
     * @param pageParam
     * @return
     */
    private NativeSearchQuery createQueryCriteria(PostQueryCriteria criteria, Pageable pageParam) {
        // 获取类的所有的成员变量
        List<Field> fields = QueryUtils.getAllFiles(criteria.getClass(), new ArrayList<>());
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // 遍历每一个files
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(criteria);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.info("{}字段没有访问权限", field.getName());
                continue;
            }
            if (Objects.isNull(value)) {
                continue;
            }
            if (value instanceof CharSequence) {
                if (StrUtil.isBlank((CharSequence) value)) {
                    continue;
                }
            }
            Query query = field.getAnnotation(Query.class);
            if (Objects.isNull(query)) {
                log.info("{}字段并非需要索引的字段", field.getName());
                continue;
            }
            String fieldName = StrUtil.isBlank(query.value()) ? field.getName() : query.value();
            if (field.getType().equals(Boolean.class)) {
                this.createFilterQueryCriteria(boolQueryBuilder, value, fieldName);
                continue;
            }
            this.createMatchQueryCriteria(value, fieldName, query, boolQueryBuilder);

            // this.initSortCriteria(pageParam.getSort(),searchQueryBuilder);
            field.setAccessible(false);
        }
        // 过滤掉已经删除和没有通过审核的数据
        this.filterJunkData(boolQueryBuilder);
        searchQueryBuilder.withQuery(boolQueryBuilder);
        searchQueryBuilder.withPageable(pageParam.first());
        return searchQueryBuilder.build();
    }

    /**
     * 过滤已经被删除和未通过审核的数据
     *
     * @param boolQueryBuilder
     */
    private void filterJunkData(BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termQuery("has_delete", false))
                .filter(QueryBuilders.termQuery("status", true));
    }

    /**
     * 初始化过滤查询条件
     *
     * @param boolQueryBuilder
     * @param value
     * @param fieldName
     */
    private void createFilterQueryCriteria(BoolQueryBuilder boolQueryBuilder, Object value, String fieldName) {
        boolQueryBuilder.filter(QueryBuilders.termQuery(fieldName, value));
    }

    /**
     * @param value
     * @param fieldName
     * @param query
     * @param queryBuilder
     */
    private void createMatchQueryCriteria(Object value, String fieldName, Query query, BoolQueryBuilder queryBuilder) {
        if (ArrayUtil.isNotEmpty(query.blurry())) {
            // String[] blurry = query.blurry();
            queryBuilder.must(QueryBuilders.multiMatchQuery(value, query.blurry()).analyzer("ik_max_word"));
            return;
        }
        queryBuilder.must(QueryBuilders.matchQuery(fieldName, value));
    }


    @Override
    public List<PostVO> listRecommend() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery("recommend", IS_RECOMMEND));
        NativeSearchQuery query = builder.withQuery(boolQueryBuilder).addAggregation(AggregationBuilders
                .terms("postAggregation").field("category_id").size(10)).build();
        this.filterJunkData(boolQueryBuilder);
        SearchHits<EsPost> searchHits = this.elasticsearchRestTemplate.search(query, EsPost.class);
        List<PostVO> postVOList = new ArrayList<>();
        if (searchHits.getAggregations() != null) {
            ParsedLongTerms postTerms = searchHits.getAggregations().get("postAggregation");
            postTerms.getBuckets().forEach(bucket -> {
                PostVO postVO = new PostVO();
                postVO.setTotal((int) bucket.getDocCount());
                postVO.setCategoryId((Long) bucket.getKeyAsNumber());
                searchHits.getSearchHits().forEach(esPostSearchHit -> {
                    if (esPostSearchHit.getContent().getCategoryId().equals(bucket.getKey())) {
                        postVO.getPostDatas().add(esPostSearchHit.getContent());
                    }
                });
                postVOList.add(postVO);
            });
        }
        return postVOList;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void savePost(Post postEntity) {
        // 对内容进行校验
        Checker checker = this.contentCheckRemoteApi.checkTxt(postEntity.getTitle() + postEntity.getContent()).getData();
        AssertUtil.statusIsTrue(checker.getStatus(), "内容涉嫌违规");
        // 检验通过
        // TODO: 2020/11/8 通过用户的id来查询用户拥有的积分
        // .....
        // TODO: 2020/11/8 方案一：先添加，然后再扣除积分，如果积分足够，则成功，积分不够直接抛出异常
        // TODO: 2020/11/8 方案二，一步一步来
        // TODO: 2020/11/8 采用方案一
        if (postEntity.insert()) {
            // TODO: 2020/11/8 访问用户微服务，查询用户以及相关积分信息

        }

    }

    @Override
    public void updatePost(Post postEntity) {
        // 通过用户的id和帖子的id来查询数据，确保传递过来额参数的正确性
        Post post = postEntity.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, postEntity.getId())
                .eq(Post::getUserId, postEntity.getUserId()));
        AssertUtil.notNull(post);
        Checker checker = this.contentCheckRemoteApi.checkTxt(postEntity.getTitle() + postEntity.getContent()).getData();
        AssertUtil.statusIsTrue(checker.getStatus(), "内容涉嫌违规");
        postEntity.updateById();


    }

    @Override
    public void deletePost(Set<Long> ids) {
        AssertUtil.collectionsIsNotNull(ids);
        //
        // TODO: 2020/11/8 通过用户的id查询用户发的帖子
    }

    @Override
    public List<EsPostDTO> listByUserId(Long userId) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.termsQuery(MiUserConstant.USER_ID, userId));
        this.filterJunkData(boolQueryBuilder);
        NativeSearchQuery query = queryBuilder
                .withQuery(boolQueryBuilder)
                .withPageable(PageRequest.of(0, 10, Sort.Direction.DESC, "create_time")).build();
        SearchHits<EsPost> searchHits = this.elasticsearchRestTemplate.search(query, EsPost.class);
        List<EsPost> esPostList = searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        return this.postMapStruct.toDto(esPostList);
    }

    @Override
    public List<EsPostDTO> listUserFavorites(Long userId) {
        Set<Long> results = this.favoritesPostService.listFavoritesPostId(userId);
        if (CollUtil.isEmpty(results)) {
            return Collections.emptyList();
        }
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.termsQuery("post_id", results));
        this.filterJunkData(boolQueryBuilder);
        NativeSearchQuery query = queryBuilder
                .withQuery(boolQueryBuilder)
                .withPageable(PageRequest.of(0, 10, Sort.Direction.DESC, "create_time"))
                .build();
        SearchHits<EsPost> searchHits = this.elasticsearchRestTemplate.search(query, EsPost.class);
        if (!searchHits.hasSearchHits()) {
            return Collections.emptyList();
        }
        List<EsPost> postList = searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        return this.postMapStruct.toDto(postList);
    }
}
