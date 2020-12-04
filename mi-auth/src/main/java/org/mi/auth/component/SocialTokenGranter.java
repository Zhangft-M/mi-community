package org.mi.auth.component;

import org.mi.auth.model.MiUserInfo;
import org.mi.common.core.constant.MiUserConstant;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-03 14:15
 **/
public class SocialTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "verify_code";

    private static final String SOCIAL_ROLE = "social";

    public SocialTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        Long userId = Long.valueOf(requestParameters.get(MiUserConstant.USER_ID));
        String username = requestParameters.get(MiUserConstant.USER_NAME);
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(String.valueOf(Collections.singletonList(SOCIAL_ROLE)));
        MiUserInfo userInfo = new MiUserInfo(userId,username,"N/A", authorityList);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userInfo,"N/A",authorityList);
        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, authenticationToken);
    }
}
