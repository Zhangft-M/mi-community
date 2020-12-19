package org.mi.api.tool.api;

import org.mi.api.tool.api.fallback.VerifyCodeValidateRemoteApiFallback;
import org.mi.api.tool.api.fallback.contentCheckRemoteApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-19 20:11
 **/
@FeignClient(name = "MI-TOOL-SERVER",contextId = "verifyCodeValidateRemoteApi",fallback = VerifyCodeValidateRemoteApiFallback.class)
public interface VerifyCodeValidateRemoteApi {

    /**
     * 校验验证码是否正确
     * @param contact /
     * @param code /
     */
    @PostMapping("/tool/sms/validate/verifyCode")
    void validateVerifyCode(@RequestParam("contact") String contact, @RequestParam("code") String code);
}
