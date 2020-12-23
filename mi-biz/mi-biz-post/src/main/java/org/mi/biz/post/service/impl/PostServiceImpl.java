package org.mi.biz.post.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
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
import org.mi.api.user.api.MiUserRemoteApi;
import org.mi.api.user.api.UserCollRemoteApi;
import org.mi.api.user.api.UserOwnPostRemoteApi;
import org.mi.biz.post.mapper.PostMapper;
import org.mi.biz.post.service.ICommentService;
import org.mi.biz.post.service.IPostService;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.UserThumbUpConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.PageResult;
import org.mi.common.core.util.RedisUtils;
import org.mi.security.util.SecurityContextHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.mi.common.core.util.TextUtils.hideText;

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


    private final ContentCheckRemoteApi contentCheckRemoteApi;

    private final MiUserRemoteApi userRemoteApi;

    private final UserOwnPostRemoteApi userOwnPostRemoteApi;

    private final UserCollRemoteApi userCollRemoteApi;

    private ICommentService commentService;

    private final RedisUtils redisUtils;

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    private static final Boolean IS_RECOMMEND = true;

    @Autowired
    public void setCommentService(ICommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    public EsPost getDataById(Long id) {
        AssertUtil.idIsNotNull(id);
        Long userId = SecurityContextHelper.getUserId();
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        SearchHit<EsPost> one = this.elasticsearchRestTemplate.searchOne(builder.withQuery(QueryBuilders.termQuery("_id", id)).build(), EsPost.class);
        assert one != null;
        AssertUtil.notNull(one.getContent());
        if (one.getContent().getPoint() > 0 && one.getContent().getCategoryId().equals(2L)) {
            // 查询数据库,看用户是否已经有阅读该贴的权限
            Long ableReadPostId = this.userOwnPostRemoteApi.getOwnPost(id);
            if (null == ableReadPostId) {
                // 没有数据,判断用户是否有足够的积分阅读
                this.userRemoteApi.updateUserPoint(0,one.getContent().getPoint(),SecurityContextHelper.getUserId());
                this.userRemoteApi.updateUserPoint(one.getContent().getPoint(),0,one.getContent().getUserId());
                this.userOwnPostRemoteApi.addUserOwnPost(id,one.getContent().getPoint());
            }
        }
        // 查询缓存，记录是否有点赞记录,当进行点赞操作的时候直接操作缓存,将数据的点赞数加一
        Integer count = (Integer) this.redisUtils.get(UserThumbUpConstant.CONTENT_THUMB_UP_NUM_PREFIX + id);
        if (!Objects.isNull(count)) {
            // 更新数据
            one.getContent().setVoteUp(count);
        } else {
            // 保存一份缓存
            this.redisUtils.set(UserThumbUpConstant.CONTENT_THUMB_UP_NUM_PREFIX + id, one.getContent().getVoteUp());
        }
        return one.getContent();
    }

    @Override
    public PageResult list(PostQueryCriteria criteria, Pageable pageParam) {

        // Page<EsPost> pageResult = this.postDAO.findByCategoryIdOrTitleOrUsername(criteria.getId(), criteria.getTitle(), criteria.getUsername(), pageRequest);
        NativeSearchQuery query = this.createQueryCriteria(criteria, pageParam);
        SearchHits<EsPost> searchResults = this.elasticsearchRestTemplate.search(query, EsPost.class);
        List<EsPost> postEntityList = searchResults.stream().map(SearchHit::getContent).collect(Collectors.toList());
        this.dealContent(postEntityList);
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
        postVOList.forEach(postVO -> {
            this.dealContent(postVO.getPostDatas());
        });
        return postVOList;
    }

    /**
     * 处理内容文本,将多余30个字符的文本用。。。。代替
     * @param postDatas
     */
    private void dealContent(List<EsPost> postDatas) {
        postDatas.forEach(data-> data.setContent(hideText(data.getContent())));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void savePost(Post postEntity) {
        // 对内容进行校验
        Checker checker = this.contentCheckRemoteApi.checkTxt(postEntity.getTitle() + postEntity.getContent());
        AssertUtil.statusIsTrue(checker.getStatus(), "内容涉嫌违规");
        postEntity.setStatus(checker.getStatus());
        // 检验通过
        // TODO: 2020/11/8 通过用户的id来查询用户拥有的积分
        // .....
        // TODO: 2020/11/8 方案一：先添加，然后再扣除积分，如果积分足够，则成功，积分不够直接抛出异常
        // TODO: 2020/12/13 方案二:先查询用户的积分,如果积分足够，扣除积分，添加数据,积分不足够直接抛出异常
        // 方案一不需要解决分布式事务问题,如果扣除积分失败直接抛出异常,这边捕获异常回滚事务
        // 方案二需要解决分布式事务问题,扣除积分成功后，如果插入post数据失败,则双方都需要回滚.
        // TODO: 2020/11/8 采用方案一
        // 先开启事务添加数据
        if (postEntity.insert()) {
            if (postEntity.getPoint() > 0 && postEntity.getCategoryId().equals(1L)) {
                this.userRemoteApi.updateUserPoint(0, postEntity.getPoint(), postEntity.getUserId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePost(Post postEntity) {
        // 通过用户的id和帖子的id来查询数据，确保传递过来额参数的正确性
        Post oldPost = postEntity.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, postEntity.getId())
                .eq(Post::getUserId, postEntity.getUserId()));
        AssertUtil.notNull(oldPost);
        if (postEntity.getCategoryId().equals(oldPost.getCategoryId())){
            throw new IllegalParameterException("类别不能修改哦!");
        }
        Checker checker = this.contentCheckRemoteApi.checkTxt(postEntity.getTitle() + postEntity.getContent());
        AssertUtil.statusIsTrue(checker.getStatus(), "内容涉嫌违规!");
        postEntity.updateById();
        if ((!oldPost.getPoint().equals(postEntity.getPoint())) && oldPost.getCategoryId().equals(1L)){
            // 更改积分,只扣除提问类别的积分
            this.userRemoteApi.updateUserPoint(oldPost.getPoint(), postEntity.getPoint(), postEntity.getUserId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Set<Long> ids, Long userId) {
        AssertUtil.collectionsIsNotNull(ids);
        //
        // TODO: 2020/11/8 通过用户的id和帖子的Id删除帖子,保证数据的准确性
        ids.forEach(id->{
            this.baseMapper.delete(Wrappers.<Post>lambdaUpdate()
                    .eq(Post::getId,id)
                    .eq(Post::getUserId,userId));
        });
    }

    @Override
    public List<EsPostDTO> listByUserId(Long userId) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.termsQuery(MiUserConstant.USER_ID, userId.toString()));
        this.filterJunkData(boolQueryBuilder);
        NativeSearchQuery query = queryBuilder
                .withQuery(boolQueryBuilder)
                .withPageable(PageRequest.of(0, 10, Sort.Direction.DESC, "create_time")).build();
        SearchHits<EsPost> searchHits = this.elasticsearchRestTemplate.search(query, EsPost.class);
        List<EsPost> esPostList = searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        return this.postMapStruct.toDto(esPostList);
    }

    // TODO: 2020/12/21 未测试
    @Override
    public List<EsPostDTO> listUserFavorites(Long userId) {
        Set<Long> results = this.userCollRemoteApi.listUserCollectPostId();
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePostByUserId(Long userId) {
        List<Post> posts = this.baseMapper.selectList(Wrappers.<Post>lambdaQuery().eq(Post::getUserId, userId));
        if (CollUtil.isEmpty(posts)){
            return;
        }
        posts.forEach(post -> {
            this.commentService.deleteCommentByPostId(post.getId());
        });
        this.baseMapper.delete(Wrappers.<Post>lambdaUpdate().eq(Post::getUserId,userId));
    }

}
