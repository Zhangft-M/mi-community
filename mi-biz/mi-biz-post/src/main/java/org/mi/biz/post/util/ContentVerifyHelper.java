package org.mi.biz.post.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.mi.api.post.entity.Post;
import org.mi.common.core.constant.ContentCheckConstant;
import org.mi.common.core.exception.ContentNotSaveException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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

    @SneakyThrows
    public  <T> void checkContent(T t,String content) {
        // 获取请求对象
        TextScanRequest textScanRequest = getTextScanRequest(content);
        // 发送请求
        HttpResponse response = acsClient.doAction(textScanRequest);
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
        }

    }

    @SneakyThrows
    private  <T> void dealResult(String suggestion, T t) {
        Class<?> clazz = t.getClass();
        Method setStatus = clazz.getMethod("setStatus",Boolean.class);
        Method setHasDelete = clazz.getMethod("setHasDelete",Boolean.class);
        switch (suggestion) {
            case ContentCheckConstant.PASS:
                setStatus.invoke(t,true);
                break;
            case ContentCheckConstant.REVIEW:
                setStatus.invoke(t,false);
                setHasDelete.invoke(t, false);
                break;
            case ContentCheckConstant.BLOCK:
                setHasDelete.invoke(t, true);
                throw new ContentNotSaveException(HttpStatus.BAD_REQUEST.value(), "内容有违法信息，请更改");
            default:
                break;
        }
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
