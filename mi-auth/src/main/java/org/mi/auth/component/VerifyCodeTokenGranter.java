package org.mi.auth.component;

import org.mi.api.user.api.MiUserRemoteApi;
import org.mi.api.user.entity.MiRole;
import org.mi.api.user.entity.MiUser;
import org.mi.auth.model.MiUserInfo;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.util.RedisUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-29 19:04
 **/
public class VerifyCodeTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "verify_code";



    private final MiUserRemoteApi miUserRemoteApi;

    private final RedisUtils redisUtils;


    public VerifyCodeTokenGranter(AuthorizationServerTokenServices tokenServices,
                                  ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, MiUserRemoteApi miUserRemoteApi, RedisUtils redisUtils) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.miUserRemoteApi = miUserRemoteApi;
        this.redisUtils = redisUtils;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        String phoneNumber = tokenRequest.getRequestParameters().get("phoneNumber");
        // 根据电话号码查询用户
        String requestCredentials = UUID.randomUUID().toString();
        this.redisUtils.set(requestCredentials,requestCredentials,30, TimeUnit.MINUTES);
        MiUser miUser = this.miUserRemoteApi.loadUserByUsername(phoneNumber, 1, SecurityConstant.FROM_IN,requestCredentials).getBody();
        if (miUser == null){
            throw new IllegalParameterException("手机号未注册或者绑定,请先注册或者绑定");
        }
        if (!miUser.getStatus()) {
            throw new RuntimeException("账号被锁定");
        }
        if (miUser.getHasDelete()) {
            throw new RuntimeException("账户不存在");
        }
        Set<String> collect = miUser.getRoles().stream().map(MiRole::getRoleName).collect(Collectors.toSet());
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(collect.toArray(collect.toArray(new String[0])));
        MiUserInfo userInfo = new MiUserInfo(miUser.getId(),miUser.getUsername(),"N/A",
                !miUser.getHasDelete(),true,true,miUser.getStatus(), authorityList);
        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userInfo,"N/A",authorityList);
        return new OAuth2Authentication(storedOAuth2Request, authenticationToken);
    }
}
