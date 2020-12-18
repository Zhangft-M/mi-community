package org.mi.biz.user.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.mi.api.user.dto.MiUserDTO;
import org.mi.api.user.entity.MiUser;
import org.mi.biz.user.service.IMiUserService;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
import org.mi.security.annotation.Anonymous;
import org.mi.security.annotation.Inner;
import org.mi.security.util.SecurityContextHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-26 18:31
 **/
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class MiUserController {

    private final IMiUserService miUserService;

    private final RocketMQTemplate rocketMQTemplate;



    @Inner
    @GetMapping("{type}")
    public ResponseEntity<MiUser> loadUserByUsername(@RequestParam String certificate, @PathVariable Integer type){
        MiUser user = this.miUserService.loadUserByUsername(certificate,type);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/info/{userId}")
    public R<MiUserDTO> getUserInfo(@PathVariable Long userId){
        if (userId == null){
            userId = SecurityContextHelper.getUserId();
        }
        return R.success(this.miUserService.getUserInfo(userId));
    }

    @Anonymous
    @PostMapping
    public R<Void> register(@RequestBody MiUser miUser){
        AssertUtil.idIsNotNull(miUser.getId());
        this.miUserService.register(miUser);
        return R.success();
    }

    @Inner
    @PutMapping
    public R<Void> updateLoginInfo(@RequestBody Map<String,Object> loginInfo){
        MiUser miUser = BeanUtil.mapToBean(loginInfo, MiUser.class, true);
        miUser.setId(Long.valueOf(loginInfo.get(MiUserConstant.USER_ID).toString()));
        AssertUtil.notNull(miUser.getId(),miUser.getLastLoginIp(),miUser.getLastLoginTime());
        if (miUser.updateById()) {
            return R.success();
        }
        return R.fail();
    }

    @PutMapping("update")
    public R<Void> updateUserInfo(@RequestBody MiUser user){
        AssertUtil.idIsNull(user.getId());
        Long userId = SecurityContextHelper.getUserId();
        user.setId(userId);
        if (user.updateById()) {
            return R.success();
        }
        return R.fail();
    }

    /**
     * 更新用户的积分
     * @param oldPoint/
     * @param newPoint/
     * @return
     */
    @PutMapping("point")
    public R<Void> updateUserPoint(Integer oldPoint,Integer newPoint){
        Long userId = SecurityContextHelper.getUserId();
        this.miUserService.updateUserPoint(oldPoint,newPoint,userId);
        return R.success();
    }

    @PutMapping("changePassword")
    public R<Void> changePassword(String newPassword){
        AssertUtil.notBlank(newPassword);
        Long userId = SecurityContextHelper.getUserId();
        this.miUserService.changePassword(newPassword,userId);
        return R.success();
    }



}
