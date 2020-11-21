package org.mi.biz.post.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.common.mp.annotation.Query;
import org.common.mp.util.QueryUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.mi.api.post.entity.EsPost;
import org.mi.api.post.entity.Post;
import org.mi.api.post.query.PostQueryCriteria;
import org.mi.api.post.vo.PostVO;
import org.mi.biz.post.mapper.PostMapper;
import org.mi.biz.post.service.IPostService;
import org.mi.common.core.constant.ContentCheckConstant;
import org.mi.common.core.exception.ContentNotSaveException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.PageResult;
import org.springframework.data.domain.Pageable;
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

    private final IAcsClient acsClient;

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    private static final Boolean IS_RECOMMEND = true;

    @Override
    public EsPost getDataById(Long id) {
        AssertUtil.idIsNotNull(id);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        SearchHit<EsPost> one = this.elasticsearchRestTemplate.searchOne(builder.withQuery(QueryBuilders.termQuery("_id", id)).build(), EsPost.class);
        return Optional.ofNullable(one.getContent()).orElseGet(EsPost::new);
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

    /*private void initSortCriteria(String sort, NativeSearchQueryBuilder searchQueryBuilder) {
        if (StrUtil.isBlank(sort)){
            log.info("排序字段为空,默认使用日期降序排序");
            searchQueryBuilder.withSort(SortBuilders.fieldSort("create_time").order(SortOrder.DESC));
            return;
        }
        // 默认第一个为排序字段，第二个为排序规则
        String[] sorts = sort.split(",");
        SortOrder sortOrder = SortOrder.DESC.toString().equalsIgnoreCase(sorts[1]) ? SortOrder.DESC : SortOrder.ASC;
        searchQueryBuilder.withSort(SortBuilders.fieldSort(sorts[0]).order(sortOrder));
    }*/

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
        AssertUtil.idIsNull(postEntity.getId());
        // 对内容进行校验
        this.checkContent(postEntity);
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
        AssertUtil.idIsNotNull(postEntity.getId());
        // 通过用户的id和帖子的id来查询数据，确保传递过来额参数的正确性

        this.checkContent(postEntity);
        if (postEntity.updateById()) {
            // TODO: 2020/11/8 访问用户微服务，查询用户以及相关积分信息
        }
    }

    @Override
    public void deletePost(Set<Long> ids) {
        AssertUtil.collectionsIsNotNull(ids);
        //
        // TODO: 2020/11/8 通过用户的id查询用户发的帖子
    }


    @SneakyThrows
    private void checkContent(Post postEntity) {
        // 获取请求对象
        TextScanRequest textScanRequest = this.getTextScanRequest(postEntity);
        // 发送请求
        HttpResponse response = this.acsClient.doAction(textScanRequest);
        if (response.isSuccess()) {
            // 校验成功
            // 序列化为Json对象
            JSONObject result = JSONUtil.parseObj(new String(response.getHttpContent(), StandardCharsets.UTF_8));
            if (HttpStatus.OK.value() == result.getInt(ContentCheckConstant.CODE)) {
                JSONArray taskResults = result.getJSONArray("data");
                for (Object taskResult : taskResults) {
                    if (HttpStatus.OK.value() == ((JSONObject) taskResult).getInt(ContentCheckConstant.CODE)) {
                        JSONArray sceneResults = ((JSONObject) taskResult).getJSONArray(ContentCheckConstant.RESULTS);
                        for (Object sceneResult : sceneResults) {
                            // String scene = ((JSONObject) sceneResult).getStr(ContentCheckConstant.SCENE);
                            String suggestion = ((JSONObject) sceneResult).getStr(ContentCheckConstant.SUGGESTION);
                            // 处理结果
                            this.dealResult(suggestion, postEntity);
                            if (log.isDebugEnabled()) {
                                log.debug("检测的内容:{},处理建议{}", ((JSONObject) taskResult).get("content"), suggestion);
                            }
                        }
                    } else {
                        System.out.println("task process fail:" + ((JSONObject) taskResult).get("code"));
                    }
                }
            }
        } else {
            // 检测失败
        }

    }

    /**
     * 处理内容验证后的结果
     *
     * @param suggestion
     * @param postEntity
     */
    private void dealResult(String suggestion, Post postEntity) {
        switch (suggestion) {
            case ContentCheckConstant.PASS:
                postEntity.setStatus(true);
                break;
            case ContentCheckConstant.REVIEW:
                postEntity.setStatus(false);
                postEntity.setHasDelete(false);
                break;
            case ContentCheckConstant.BLOCK:
                throw new ContentNotSaveException(HttpStatus.BAD_REQUEST.value(), "内容有违法信息，请更改");
            default:
                break;
        }
    }

    /**
     * 初始化文本验证请求对象
     *
     * @param postEntity
     * @return
     */
    @SneakyThrows
    private TextScanRequest getTextScanRequest(Post postEntity) {
        TextScanRequest textScanRequest = new TextScanRequest();
        // 指定API返回格式。
        textScanRequest.setSysAcceptFormat(FormatType.JSON);
        textScanRequest.setHttpContentType(FormatType.JSON);
        // 指定请求方法。
        textScanRequest.setSysMethod(com.aliyuncs.http.MethodType.POST);
        textScanRequest.setSysEncoding("UTF-8");
        textScanRequest.setSysRegionId("cn-shanghai");
        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
        task1.put("dataId", UUID.randomUUID().toString());
        // 检测的内容,标题加上内容,长度不超过10000个字符。
        task1.put("content", postEntity.getTitle() + postEntity.getContent());
        tasks.add(task1);
        JSONObject data = new JSONObject();
        data.set("scenes", Collections.singletonList("antispam"));
        data.set("tasks", tasks);
        textScanRequest.setHttpContent(data.toJSONString(4).getBytes(StandardCharsets.UTF_8), "UTF-8", FormatType.JSON);
        textScanRequest.setSysConnectTimeout(3000);
        textScanRequest.setSysReadTimeout(6000);
        return textScanRequest;
    }
}
