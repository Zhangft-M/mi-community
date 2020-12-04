package org.mi.auth.factory;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.mi.auth.config.SocialAuthConfig;
import org.springframework.stereotype.Component;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-02 22:24
 **/
@Component
@RequiredArgsConstructor
public class AuthRequestFactory {

    private final SocialAuthConfig socialAuthConfig;

    private volatile static AuthGiteeRequest giteeRequest;

    private volatile static AuthQqRequest authQqRequest;


    private AuthRequest createAuthRequest(String type){
        AuthDefaultSource source = this.getType(type);
        AuthConfig authConfig = this.socialAuthConfig.getType().get(source);
        switch (source){
            case GITEE:
                if (null == giteeRequest){
                    synchronized (this){
                        if (null == giteeRequest){
                            giteeRequest = new AuthGiteeRequest(authConfig);
                        }
                    }
                }
                return giteeRequest;
            case QQ:
                if (null == authQqRequest){
                    synchronized (this){
                        if (null == authQqRequest){
                            authQqRequest = new AuthQqRequest(authConfig);
                        }
                    }
                }
                return authQqRequest;
            default:
                return null;
        }
    }

    private AuthDefaultSource  getType(String type){
        if (StrUtil.isNotBlank(type)) {
            return AuthDefaultSource.valueOf(type.toUpperCase());
        } else {
            throw new RuntimeException("不支持的类型");
        }
    }

    public AuthRequest getInstance(String type){
        return this.createAuthRequest(type);
    }
}
