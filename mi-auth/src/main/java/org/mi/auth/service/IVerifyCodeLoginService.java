package org.mi.auth.service;

import org.mi.auth.model.LoginParams;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-29 18:28
 **/
public interface IVerifyCodeLoginService {
    /**
     * 验证码登录
     * @param loginParams
     * @return
     */
    OAuth2AccessToken verifyCodeLogin(LoginParams loginParams);
}
