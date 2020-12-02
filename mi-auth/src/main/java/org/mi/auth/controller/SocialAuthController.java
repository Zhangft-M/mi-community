package org.mi.auth.controller;

import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
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

    /*private final AuthRequest authRequest;

    private final ISocialAuthService socialAuthService;

    @RequestMapping("/render/gitee")
    public void renderAuth(HttpServletResponse response) throws IOException {
        // String authorize = authRequest.authorize(AuthStateUtils.createState());
        // return authorize;
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @RequestMapping("/callback/gitee")
    public AuthUser login(AuthCallback callback) {
        AuthUser socialUser = (AuthUser) authRequest.login(callback).getData();
        this.socialAuthService.auth(socialUser);
        return socialUser;
    }*/

}
