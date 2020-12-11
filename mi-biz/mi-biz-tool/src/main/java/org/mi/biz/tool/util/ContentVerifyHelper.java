package org.mi.biz.tool.util;

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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mi.api.tool.entity.Checker;
import org.mi.common.core.constant.ContentCheckConstant;
import org.mi.common.core.exception.ContentNotSaveException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

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


    public Checker checkTxtContent(String content,Long userId) {
        // 获取请求对象
        TextScanRequest textScanRequest = getTextScanRequest(content);
        // 发送请求
        try {
            HttpResponse response = this.acsClient.doAction(textScanRequest);
            Checker checker = new Checker();
            checker.setTxt(content);
            checker.setType(0);
            checker.setUserId(userId);
            dealResponse(checker, response);
            return checker;
        } catch (ClientException e) {
            log.error("文本检验失败:{}", e.getErrMsg());
            throw new RuntimeException("文本检验失败");
        }
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
    public Checker checkImageContent(String url, Long userId) {
        ImageSyncScanRequest imageSyncScanRequest = this.getImageSyncScanRequest(url);
        try {
            HttpResponse httpResponse = this.acsClient.doAction(imageSyncScanRequest);
            Checker checker = new Checker();
            checker.setUrl(url);
            checker.setType(1);
            checker.setUserId(userId);
            this.dealResponse(checker, httpResponse);
            return checker;
        } catch (ClientException e) {
            log.error("图片检验失败:{}", e.getErrMsg());
            throw new RuntimeException("图片检验失败");
        }
    }

    @SneakyThrows
    private void dealResult(String suggestion, Checker checker) {
        switch (suggestion) {
            case ContentCheckConstant.PASS:
                checker.setStatus(true);
                break;
            case ContentCheckConstant.REVIEW:
                checker.setStatus(false);
                checker.insert();
                throw new ContentNotSaveException("内容涉嫌违规,人工审核中");
            case ContentCheckConstant.BLOCK:
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
}
