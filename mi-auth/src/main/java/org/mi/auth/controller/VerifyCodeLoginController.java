package org.mi.auth.controller;

import lombok.RequiredArgsConstructor;
import org.mi.auth.model.LoginParams;
import org.mi.auth.service.IVerifyCodeLoginService;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

/**
 * @program: mi-community
 * @description: 验证码登录授权
 * @author: Micah
 * @create: 2020-11-29 18:20
 **/
@RestController
@RequestMapping("/verifyCode")
@RequiredArgsConstructor
public class VerifyCodeLoginController {

    private final IVerifyCodeLoginService verifyCodeLoginService;

    @PostMapping("login")
    public R<OAuth2AccessToken> verifyCodeLogin(LoginParams loginParams){
        // AssertUtil.isPhoneNumber(loginParams.getPhoneNumber());
        OAuth2AccessToken auth2AccessToken = this.verifyCodeLoginService.verifyCodeLogin(loginParams);
        return R.success(auth2AccessToken);
    }
}
