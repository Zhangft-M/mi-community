package org.mi.auth.controller;

import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.mi.auth.factory.AuthRequestFactory;
import org.mi.auth.service.ISocialAuthService;
import org.mi.common.core.exception.util.AssertUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: mi-community
 * @description: 第三方认证接口
 * @author: Micah
 * @create: 2020-11-05 18:05
 **/
@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
public class SocialAuthController {

    private final AuthRequestFactory authRequestFactory;

    private final ISocialAuthService socialAuthService;

    @RequestMapping("/render/{type}")
    public void renderAuth(HttpServletResponse response, @PathVariable String type) throws IOException {
        // String authorize = authRequest.authorize(AuthStateUtils.createState());
        // return authorize;
        AuthRequest authRequest = this.authRequestFactory.getInstance(type);
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @RequestMapping("/callback/{type}")
    public AuthUser login(AuthCallback callback, @PathVariable String type) {
        AuthRequest authRequest = this.authRequestFactory.getInstance(type);
        AuthUser socialUser = (AuthUser) authRequest.login(callback).getData();
        this.socialAuthService.auth(socialUser);
        return socialUser;
    }



}
