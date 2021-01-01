package org.mi.biz.user.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.mi.api.user.dto.MiUserDTO;
import org.mi.api.user.entity.MiUser;
import org.mi.biz.user.service.IMiUserService;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
import org.mi.common.core.util.RedisUtils;
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

    private final RedisUtils redisUtils;




    @Inner
    @GetMapping("{type}")
    public ResponseEntity<MiUser> loadUserByUsername(@RequestParam String certificate, @PathVariable Integer type){
        MiUser user = this.miUserService.loadUserByUsername(certificate,type);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/info")
    public R<MiUserDTO> getUserInfo(@RequestParam(required = false) Long userId){
        if (userId == null){
            userId = SecurityContextHelper.getUserId();
        }
        return R.success(this.miUserService.getUserInfo(userId));
    }

    @Anonymous
    @PostMapping("register")
    public R<Void> register(MiUser miUser){
        AssertUtil.idIsNull(miUser.getId());
        this.miUserService.register(miUser);
        return R.success();
    }

    @Inner
    @PutMapping
    public R<Void> updateLoginInfo(@RequestBody Map<String,Object> loginInfo){
        MiUser miUser = BeanUtil.mapToBean(loginInfo, MiUser.class, true);
        AssertUtil.notNull(miUser.getLastLoginIp(),miUser.getLastLoginTime());
        MiUser oldUser = this.miUserService.getById(Long.valueOf(loginInfo.get(MiUserConstant.USER_ID).toString()));
        AssertUtil.notNull(oldUser);
        boolean hasLogin = this.redisUtils.hasKey(RedisCacheConstant.USER_TODAY_HAS_LOGIN + oldUser.getId());
        if (!hasLogin) {
            oldUser.setPoint(oldUser.getPoint() + 10);
            this.redisUtils.set(RedisCacheConstant.USER_TODAY_HAS_LOGIN + oldUser.getId(),oldUser.getLastLoginIp());
        }
        oldUser.setLastLoginIp(miUser.getLastLoginIp());
        oldUser.setLastLoginTime(miUser.getLastLoginTime());
        if (oldUser.updateById()) {
            return R.success();
        }
        return R.fail();
    }

    /*@PutMapping("update/{verifyCode}")
    public R<MiUserDTO> updateUserInfo(@RequestBody MiUser user, @PathVariable(required = false,value = "verifyCode") String code){
        AssertUtil.idIsNull(user.getId());
        Long userId = SecurityContextHelper.getUserId();
        user.setId(userId);
        MiUserDTO userDTO = this.miUserService.updateUserInfo(user,code);
        return R.success(userDTO);
    }*/

    @PutMapping("update")
    public R<MiUserDTO> update(MiUser user, @RequestParam(value = "verifyCode",required = false) String code){
        AssertUtil.idIsNull(user.getId());
        Long userId = SecurityContextHelper.getUserId();
        user.setId(userId);
        MiUserDTO userDTO = this.miUserService.updateUserInfo(user,code);
        return R.success(userDTO);
    }

    /**
     * 更新用户的积分
     * @param oldPoint/
     * @param newPoint/
     * @return
     */
    @Inner
    @PutMapping("point")
    public R<Void> updateUserPoint(Integer oldPoint,Integer newPoint,Long userId){
        if (userId == null) {
            userId = SecurityContextHelper.getUserId();
        }
        this.miUserService.updateUserPoint(oldPoint,newPoint,userId);
        return R.success();
    }

    @DeleteMapping("cancel")
    public R<Void> deleteUser(@RequestParam("phoneNumber")String phoneNumber,
                              @RequestParam("verifyCode")String verifyCode){
        Long userId = SecurityContextHelper.getUserId();
        this.miUserService.deleteUser(userId,phoneNumber,verifyCode);
        return R.success();
    }

    @PutMapping("changePassword")
    public R<Void> changePassword(@RequestParam("password") String newPassword){
        AssertUtil.notBlank(newPassword);
        Long userId = SecurityContextHelper.getUserId();
        this.miUserService.changePassword(newPassword,userId);
        return R.success();
    }

    @Inner
    @PutMapping("/postCount/increment")
    public R<Void> incrementUserPostCount(@RequestParam("userId") Long userId){
        this.miUserService.incrementUserPostCount(userId);
        return R.success();
    }


    /**
     * 修改密码校验用户的手机号是否正确
     * @param phoneNumber
     * @param code
     * @return
     */
    @PostMapping("/checkUser/phoneNumber")
    public R<Void> checkUserPhoneNumber(@RequestParam String phoneNumber,@RequestParam("verifyCode") String code){
        Long userId = SecurityContextHelper.getUserId();
        this.miUserService.checkUserPhoneNumber(userId,phoneNumber,code);
        return R.success();
    }

}
