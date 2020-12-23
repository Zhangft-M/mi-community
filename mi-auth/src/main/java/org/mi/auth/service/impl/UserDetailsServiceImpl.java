package org.mi.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.mi.api.user.api.MiUserRemoteApi;
import org.mi.api.user.entity.MiRole;
import org.mi.api.user.entity.MiUser;
import org.mi.auth.model.MiUserInfo;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.util.RedisUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-05 15:31
 **/
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MiUserRemoteApi miUserRemoteApi;

    private final RedisUtils redisUtils;

    @Override
    public UserDetails loadUserByUsername(String certificate) throws UsernameNotFoundException {
        String requestCredentials = UUID.randomUUID().toString();
        this.redisUtils.set(requestCredentials,requestCredentials,30, TimeUnit.MINUTES);
        MiUser user = this.miUserRemoteApi.loadUserByUsername(certificate, 0, SecurityConstant.FROM_IN,requestCredentials).getBody();
        if (user == null){
            throw new IllegalArgumentException("该用户名未注册,请先注册");
        }
        Set<String> collect = user.getRoles().stream().map(MiRole::getRoleName).collect(Collectors.toSet());
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(collect.toArray(collect.toArray(new String[0])));
        return new MiUserInfo(user.getId(), certificate, user.getPassword(), !user.getHasDelete(), true, true, user.getStatus(), authorityList);
    }
}
