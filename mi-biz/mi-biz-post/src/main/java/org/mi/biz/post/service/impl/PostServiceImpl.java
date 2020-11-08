package org.mi.biz.post.service.impl;

import cn.hutool.json.JSON;
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
import org.common.mp.component.PageParam;
import org.mi.api.post.entity.EsPostEntity;
import org.mi.api.post.entity.PostEntity;
import org.mi.api.post.query.PostQueryCriteria;
import org.mi.biz.post.dao.PostDAO;
import org.mi.biz.post.mapper.PostMapper;
import org.mi.biz.post.service.IPostService;
import org.mi.common.core.constant.ContentCheckConstant;
import org.mi.common.core.exception.ContentNotSaveException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 18:04
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, PostEntity> implements IPostService {

    private final PostDAO postDAO;

    private final IAcsClient acsClient;

    @Override
    public PageResult list(PostQueryCriteria criteria, PageParam pageParam) {
        // AssertUtil.notNull(pageParam.getCurrentPage(),pageParam.getSize(),pageParam.getSort());
        String[] params = pageParam.getSort().split(",");
        Sort.Direction direction = Sort.Direction.ASC.name().equalsIgnoreCase(params[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, params[0]);
        PageRequest pageRequest = PageRequest.of(pageParam.getCurrentPage().intValue() - 1,
                pageParam.getSize().intValue(), sort);
        Page<EsPostEntity> pageResult = this.postDAO.findByCategoryIdOrTitleOrUsername(criteria.getId(), criteria.getTitle(), criteria.getUsername(), pageRequest);
        return PageResult.of(pageResult.getTotalElements(), pageResult.getContent());
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void savePost(PostEntity postEntity) {
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
    public void updatePost(PostEntity postEntity) {
        AssertUtil.idIsNotNull(postEntity.getId());
        // 通过用户的id和帖子的id来查询数据，确保传递过来额参数的正确性

        this.checkContent(postEntity);
        if (postEntity.updateById()){
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
    private void checkContent(PostEntity postEntity) {
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
                            if (log.isDebugEnabled()){
                                log.debug("检测的内容:{},处理建议{}",((JSONObject) taskResult).get("content"),suggestion);
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

    private void dealResult(String suggestion, PostEntity postEntity) {
        switch (suggestion) {
            case ContentCheckConstant.PASS:
                postEntity.setStatus(true);
                break;
            case ContentCheckConstant.REVIEW:
                postEntity.setStatus(false);
                postEntity.setDelete(false);
                break;
            case ContentCheckConstant.BLOCK:
                throw new ContentNotSaveException(HttpStatus.BAD_REQUEST.value(), "内容有违法信息，请更改");
            default:
                break;
        }
    }

    @SneakyThrows
    private TextScanRequest getTextScanRequest(PostEntity postEntity) {
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
