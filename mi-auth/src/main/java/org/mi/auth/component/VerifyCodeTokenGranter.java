package org.mi.auth.component;

import org.mi.api.user.api.MiUserRemoteApi;
import org.mi.api.user.entity.MiRole;
import org.mi.api.user.entity.MiUser;
import org.mi.auth.model.MiUserInfo;
import org.mi.common.core.exception.IllegalParameterException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.List;
import java.util.Set;
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


    public VerifyCodeTokenGranter(AuthorizationServerTokenServices tokenServices,
                                  ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, MiUserRemoteApi miUserRemoteApi) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.miUserRemoteApi = miUserRemoteApi;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        String phoneNumber = tokenRequest.getRequestParameters().get("phoneNumber");
        // 根据电话号码查询用户
        MiUser miUser = this.miUserRemoteApi.loadUserByUsername(phoneNumber, 1,"Y").getBody();
        if (miUser == null){
            throw new IllegalParameterException("手机号未注册或者绑定,请先注册或者绑定");
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
