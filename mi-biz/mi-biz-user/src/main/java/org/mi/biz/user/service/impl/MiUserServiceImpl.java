package org.mi.biz.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mi.api.user.dto.MiUserDTO;
import org.mi.api.user.entity.MiUser;
import org.mi.api.user.entity.MiUserRole;
import org.mi.api.user.mapstruct.MiUserMapStruct;
import org.mi.biz.user.mapper.MiUserMapper;
import org.mi.biz.user.service.IMiUserService;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-26 19:01
 **/
@Slf4j
@Service
@CacheConfig(cacheNames = "user")
@RequiredArgsConstructor
public class MiUserServiceImpl extends ServiceImpl<MiUserMapper, MiUser> implements IMiUserService {

    private final MiUserMapper miUserMapper;

    private final PasswordEncoder passwordEncoder;

    private final MiUserMapStruct miUserMapStruct;

    private final RedisUtils redisUtils;

    @Override
    public MiUser loadUserByUsername(String certificate, Integer type) {
        AssertUtil.notBlank(certificate);
        MiUser user = this.miUserMapper.selectUserWithRoleByCertificate(certificate,type);
        return Optional.ofNullable(user).orElseGet(MiUser::new);
    }

    @Override
    // @Cacheable(key = MiUserConstant.USER_INFO_CACHE_KEY + "#p0")
    public MiUserDTO getUserInfo(Long userId) {
        MiUser user = this.getById(userId);
        return Optional.ofNullable(this.miUserMapStruct.toDto(user)).orElseGet(MiUserDTO::new);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(MiUser miUser) {
        AssertUtil.notBlank(miUser.getPhone(),miUser.getUsername(),miUser.getPassword());

        MiUser user = miUser.selectOne(Wrappers.<MiUser>lambdaQuery().eq(MiUser::getUsername, miUser.getUsername()));
        if (BeanUtil.isNotEmpty(user)){
            // 用户名已经存在
            throw new IllegalParameterException("用户名已经存在");
        }
        miUser.setPassword(this.passwordEncoder.encode(miUser.getPassword()));
        if(miUser.insert()){
            MiUserRole miUserRole = new MiUserRole();
            miUserRole.setRoleId(1);
            miUserRole.setUserId(miUser.getId());
            miUserRole.insert();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserPoint(Integer oldPoint, Integer newPoint, Long userId) {
        MiUser user = this.miUserMapper.selectById(userId);
        AtomicInteger atomicInteger = new AtomicInteger();
        atomicInteger.set(user.getPoint() + oldPoint);
        if (atomicInteger.get() < newPoint){
           throw new IllegalParameterException("积分不足");
        }
        // 进行更新操作
        user.setPoint(atomicInteger.get() - newPoint);
        user.updateById();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String newPassword, Long userId) {
        MiUser miUser = this.miUserMapper.selectById(userId);
        boolean matches = this.passwordEncoder.matches(newPassword, miUser.getPassword());
        if (matches){
            throw new IllegalParameterException("与原来密码相同");
        }
        miUser.setPassword(this.passwordEncoder.encode(newPassword));
        miUser.updateById();
    }
}
