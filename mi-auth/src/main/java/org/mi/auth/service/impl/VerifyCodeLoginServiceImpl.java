package org.mi.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mi.auth.config.Oauth2AuthorizationServerConfig;
import org.mi.auth.model.LoginParams;
import org.mi.auth.service.IVerifyCodeLoginService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.endpoint.AbstractEndpoint;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-29 18:32
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyCodeLoginServiceImpl implements IVerifyCodeLoginService {

    private final ClientDetailsService clientDetailsService;

    private final OAuth2RequestFactory oAuth2RequestFactory;

    private final TokenGranter tokenGranter;


    private OAuth2RequestValidator oAuth2RequestValidator = new DefaultOAuth2RequestValidator();


    @Override
    public OAuth2AccessToken verifyCodeLogin(LoginParams loginParams) {
        // 验证客户端是否正确
        ClientDetails clientDetails = this.clientDetailsService.loadClientByClientId(loginParams.getClientId());
        HashMap<String, String> map = Maps.newHashMapWithExpectedSize(3);
        map.put(OAuth2Utils.CLIENT_ID, loginParams.getClientId());
        map.put(OAuth2Utils.GRANT_TYPE,loginParams.getGrantType());
        map.put(OAuth2Utils.SCOPE,loginParams.getScope());
        map.put("phoneNumber",loginParams.getPhoneNumber());
        TokenRequest tokenRequest = oAuth2RequestFactory.createTokenRequest(map, clientDetails);
        if (StrUtil.isNotBlank(loginParams.getClientId())) {
            // Only validate the client details if a client authenticated during this
            // request.
            if (!loginParams.getClientId().equals(tokenRequest.getClientId())) {
                // double check to make sure that the client ID in the token request is the same as that in the
                // authenticated client
                throw new InvalidClientException("Given client ID does not match authenticated client");
            }
        }
        if (clientDetails != null) {
            oAuth2RequestValidator.validateScope(tokenRequest, clientDetails);
        }
        if (!StringUtils.hasText(tokenRequest.getGrantType())) {
            throw new InvalidRequestException("Missing grant type");
        }
        if (tokenRequest.getGrantType().equals("implicit")) {
            throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
        }
        OAuth2AccessToken accessToken = this.tokenGranter.grant(tokenRequest.getGrantType(), tokenRequest);
        return accessToken;
    }
}
