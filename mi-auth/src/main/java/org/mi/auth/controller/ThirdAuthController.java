package org.mi.auth.controller;

import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
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
@RequestMapping("/third")
@RequiredArgsConstructor
public class ThirdAuthController {

    private final AuthRequest authRequest;

    @RequestMapping("/render/gitee")
    public void renderAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @RequestMapping("/callback/gitee")
    public Object login(AuthCallback callback) {
        return authRequest.login(callback);
    }

}
