package org.mi.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.mi.api.user.api.MiUserRemoteApi;
import org.mi.api.user.entity.MiRole;
import org.mi.api.user.entity.MiUser;
import org.mi.auth.model.MiUserInfo;
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

    @Override
    public UserDetails loadUserByUsername(String certificate) throws UsernameNotFoundException {
        MiUser user = this.miUserRemoteApi.loadUserByUsername(certificate, 0,"Y").getBody();
        if (user == null){
            throw new IllegalArgumentException("该用户名未注册,请先注册");
        }
        Set<String> collect = user.getRoles().stream().map(MiRole::getRoleName).collect(Collectors.toSet());
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(collect.toArray(collect.toArray(new String[0])));
        MiUserInfo miUserInfo = new MiUserInfo(user.getId(), certificate, user.getPassword(), !user.getHasDelete(), true, true, user.getStatus(), authorityList);
        /*SimpleGrantedAuthority admin = new SimpleGrantedAuthority("admin");
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(admin);
        return new User("admin", "$2a$10$9CZaQm4CuZiojaYRJ2dLEu87FGGJj2Q9V6mubZDVt1sT3wPD3LULK", list);*/
        return miUserInfo;
    }
}
