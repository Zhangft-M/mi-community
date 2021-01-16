package org.mi.gateway.component.paramhandler;

import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.mi.gateway.component.abs.AbstractParamHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: mi-community
 * @description: todo：该处理器主要是为了处理在增加一个post之后，再刷新主页请求数据时会报错400badrequest的问题，后续可以找更好的方式来解决
 * @author: Micah
 * @create: 2021-01-16 18:12
 **/
@Slf4j
@Component("addPostParamHandler")
public class AddPostParamHandler extends AbstractParamHandler {
    protected AddPostParamHandler(RSA rsa) {
        super(rsa);
    }

    @Override
    public Map<String, Object> dealAndPackageParams(Map<String, Object> attributes) {
        Object postData = attributes.get("postData");
        Map<String,Object> params = Maps.newConcurrentMap();
        if (postData instanceof JSON){
            JSON jsonPostData = (JSON) postData;
            Long categoryId = jsonPostData.getByPath("categoryId", Long.class);
            String title = jsonPostData.getByPath("title", String.class);
            String content = jsonPostData.getByPath("content", String.class);
            Boolean receiveReply = jsonPostData.getByPath("receiveReply", Boolean.class);
            Integer point = jsonPostData.getByPath("point", Integer.class);
            params.put("categoryId",categoryId);
            params.put("title",title);
            params.put("content",content);
            params.put("receiveReply",receiveReply);
            params.put("point",point);
        }
        return params;
    }
}
