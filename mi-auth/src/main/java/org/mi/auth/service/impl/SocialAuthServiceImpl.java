package org.mi.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.mi.auth.service.ISocialAuthService;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-26 14:54
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SocialAuthServiceImpl implements ISocialAuthService {


    @Override
    public void auth(AuthUser socialUser) {

    }


    private OAuth2Authentication createOAuth2Authentication(AuthUser socialUser) {

        return null;
    }
}
