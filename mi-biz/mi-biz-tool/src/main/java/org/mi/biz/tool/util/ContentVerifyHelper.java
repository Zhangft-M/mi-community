package org.mi.biz.tool.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.green.model.v20180509.ImageSyncScanRequest;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.IClientProfile;
import com.tencentcloudapi.cms.v20190321.CmsClient;
import com.tencentcloudapi.cms.v20190321.models.*;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.mi.api.tool.entity.Checker;
import org.mi.common.core.constant.ContentCheckConstant;
import org.mi.common.core.exception.ContentNotSaveException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-23 15:55
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ContentVerifyHelper {

    private final IAcsClient acsClient;

    private final CmsClient cmsClient;


    public Checker aliyunCheckTxtContent(String content,Long userId) {
        // 获取请求对象
        TextScanRequest textScanRequest = getTextScanRequest(content);
        // 发送请求
        try {
            HttpResponse response = this.acsClient.doAction(textScanRequest);
            Checker checker = initTextChecker(content, userId);
            dealResponse(checker, response);
            return checker;
        } catch (ClientException e) {
            log.error("文本检验失败:{}", e.getErrMsg());
            throw new RuntimeException("文本检验失败");
        }
    }

    private Checker initTextChecker(String content, Long userId) {
        Checker checker = new Checker();
        checker.setContent(content);
        checker.setType(0);
        checker.setUserId(userId);
        return checker;
    }

    private void dealResponse(Checker t, HttpResponse response) {
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
                            dealResult(suggestion, t);
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
            throw new RuntimeException("内容检测失败");
        }
    }

    /**
     * 检测
     *
     * @param url
     * @param userId
     */
    public Checker aliyunCheckImageContent(String url, Long userId) {
        ImageSyncScanRequest imageSyncScanRequest = this.getImageSyncScanRequest(url);
        try {
            HttpResponse httpResponse = this.acsClient.doAction(imageSyncScanRequest);
            Checker checker = initImageUrlChecker(url, userId);
            this.dealResponse(checker, httpResponse);
            return checker;
        } catch (ClientException e) {
            log.error("图片检验失败:{}", e.getErrMsg());
            throw new RuntimeException("图片检验失败");
        }
    }

    private Checker initImageUrlChecker(String url, Long userId) {
        Checker checker = new Checker();
        checker.setUrl(url);
        checker.setType(1);
        checker.setUserId(userId);
        return checker;
    }

    @SneakyThrows
    private void dealResult(String suggestion, Checker checker) {
        switch (suggestion) {
            case ContentCheckConstant.PASS:
                checker.setStatus(true);
                break;
            case ContentCheckConstant.REVIEW:
                checker.setStatus(false);
                if (null != checker.getUserId()) {
                    checker.insert();
                }
                throw new ContentNotSaveException("内容涉嫌违规,人工审核中");
            case ContentCheckConstant.BLOCK:
                checker.setHasDelete(true);
                throw new ContentNotSaveException("内容涉嫌违规,请修改");
            default:
                break;
        }
    }

    private ImageSyncScanRequest getImageSyncScanRequest(String url) {
        ImageSyncScanRequest imageSyncScanRequest = new ImageSyncScanRequest();
        // 指定API返回格式。
        imageSyncScanRequest.setSysAcceptFormat(FormatType.JSON);
        // 指定请求方法。
        imageSyncScanRequest.setSysMethod(MethodType.POST);
        imageSyncScanRequest.setSysEncoding("utf-8");
        // 支持HTTP和HTTPS。
        imageSyncScanRequest.setSysProtocol(ProtocolType.HTTP);
        JSONObject httpBody = new JSONObject();
        httpBody.putByPath("scenes", Collections.singletonList("porn"));
        JSONObject task = new JSONObject();
        task.putByPath("dataId", UUID.randomUUID().toString());
        // 设置图片链接。
        task.putByPath("url", url);
        task.putByPath("time", new Date());
        httpBody.putByPath("tasks", Collections.singletonList(task));
        imageSyncScanRequest.setHttpContent(httpBody.toJSONString(4).getBytes(StandardCharsets.UTF_8),
                "utf-8",
                FormatType.JSON);
        imageSyncScanRequest.setSysConnectTimeout(3000);
        imageSyncScanRequest.setSysReadTimeout(10000);
        return imageSyncScanRequest;
    }

    /**
     * 初始化文本验证请求对象
     *
     * @param content /
     * @return
     */
    @SneakyThrows
    private TextScanRequest getTextScanRequest(String content) {
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
        task1.put("content", content);
        tasks.add(task1);
        JSONObject data = new JSONObject();
        data.set("scenes", Collections.singletonList("antispam"));
        data.set("tasks", tasks);
        textScanRequest.setHttpContent(data.toJSONString(4).getBytes(StandardCharsets.UTF_8), "UTF-8", FormatType.JSON);
        textScanRequest.setSysConnectTimeout(3000);
        textScanRequest.setSysReadTimeout(6000);
        return textScanRequest;
    }

    /**
     * 腾讯云文本内容检测请求对象
     */
    private TextModerationRequest textModerationRequest(String content,Long userId){
        TextModerationRequest req = new TextModerationRequest();
        if (null != userId) {
            User user = new User();
            user.setUserId(String.valueOf(userId));
            user.setAccountType(7L);
            req.setUser(user);
        }
        req.setContent(content);
        return req;
    }

    /**
     * 通过图片url地址进行检测
     * @param url
     * @return
     */
    private ImageModerationRequest imageUrlModerationRequest(String url){
        ImageModerationRequest request = new ImageModerationRequest();
        request.setFileUrl(url);
        return request;
    }

    /**
     * 直接对图片的内容进行检测
     * @param content 图片进行base64加密的结果
     * @return
     */
    private ImageModerationRequest imageContentModerationRequest(String content){
        ImageModerationRequest request = new ImageModerationRequest();
        request.setFileContent(content);
        return request;
    }

    /**
     * 调用腾讯云内容安全检测接口检测文本内容
     * @param content /
     * @param userId /
     * @return /
     */
    public Checker tencentTextCheck(String content, Long userId){
        TextModerationRequest textModerationRequest = this.textModerationRequest(Base64Utils.encodeToString(content.getBytes(StandardCharsets.UTF_8)),userId);
        try {
            TextModerationResponse response = this.cmsClient.TextModeration(textModerationRequest);
            Checker checker = this.initTextChecker(content, userId);
            this.dealTencentTextCheckResponse(checker,response);
            return checker;
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException("内容检测失败");
        }
    }

    /**
     * 根据图片的url对图片进行检测
     * @param url
     * @param userId
     * @return
     */
    public Checker tencentImageUrlCheck(String url,Long userId){
        ImageModerationRequest request = this.imageUrlModerationRequest(url);
        try {
            ImageModerationResponse response = this.cmsClient.ImageModeration(request);
            Checker checker = this.initImageUrlChecker(url, userId);
            this.dealTencentImageCheckResponse(checker,response);
            return checker;
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException("图片检测失败");
        }
    }

    public Checker tencentImageContentCheck(String content,Long userId){
        ImageModerationRequest request = this.imageContentModerationRequest(content);
        try {
            ImageModerationResponse response = this.cmsClient.ImageModeration(request);
            Checker checker = this.initImageContentChecker(content,userId);
            this.dealTencentImageCheckResponse(checker,response);
            return checker;
        } catch (TencentCloudSDKException e) {
           log.warn("根据图片的内容检测图片失败:ex=>{}",e.toString());
           throw new RuntimeException("图片检测失败");
        }
    }

    private void dealTencentImageCheckResponse(Checker checker, ImageModerationResponse response) {
        ImageData data = response.getData();
        if (data.getEvilFlag() == 1){
            checker.setStatus(false);
            throw new ContentNotSaveException("内容有违法嫌疑,请修改后上传");
        }
        checker.setStatus(true);
    }

    private Checker initImageContentChecker(String content, Long userId) {
        Checker checker = new Checker();
        checker.setUserId(userId);
        checker.setType(1);
        checker.setContent(content);
        return checker;
    }


    private void dealTencentTextCheckResponse(Checker checker, TextModerationResponse response) {
        TextData data = response.getData();
        String suggestion = data.getSuggestion().toLowerCase();
        this.dealTencentCheckResult(checker,suggestion);
    }

    /**
     * 处理腾讯云检测结果
     * @param checker /
     * @param suggestion /
     */
    private void dealTencentCheckResult(Checker checker, String suggestion) {
        if (suggestion == null){
            checker.insert();
            throw new ContentNotSaveException("内容涉嫌违法,人工审核中");
        }
        this.dealResult(suggestion,checker);
    }
}
