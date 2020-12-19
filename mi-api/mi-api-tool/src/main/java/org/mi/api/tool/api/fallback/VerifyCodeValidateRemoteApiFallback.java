package org.mi.api.tool.api.fallback;

import lombok.extern.slf4j.Slf4j;
import org.mi.api.tool.api.VerifyCodeValidateRemoteApi;
import org.springframework.stereotype.Component;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-19 20:12
 **/
@Slf4j
@Component
public class VerifyCodeValidateRemoteApiFallback implements VerifyCodeValidateRemoteApi {
    @Override
    public void validateVerifyCode(String contact, String code) {
      log.warn("调用工具服务接口校验验证码失败");
    }
}
