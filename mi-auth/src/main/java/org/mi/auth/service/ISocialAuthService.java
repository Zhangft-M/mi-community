package org.mi.auth.service;

import me.zhyd.oauth.model.AuthUser;

/**
* @program: mi-community
* @description:
* @author: Micah
* @create: 2020-11-26 14:44
**/public interface ISocialAuthService {
    /**
     * 认证
     * @param socialUser
     */
    void auth(AuthUser socialUser);
}
