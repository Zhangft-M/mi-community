package org.mi.api.user.api;

import org.mi.api.user.api.fallback.MiUserRemoteApiFallback;
import org.mi.api.user.dto.MiUserDTO;
import org.mi.api.user.entity.MiUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-27 14:05
 **/
@FeignClient(name = "MI-USER-SERVER",contextId = "miUserRemoteApi",fallback = MiUserRemoteApiFallback.class)
public interface MiUserRemoteApi {
    /**
     * 根据用户名或者手机号查询用户
     * @param certificate
     * @param type
     * @return
     */
    @GetMapping("/user/{type}")
    ResponseEntity<MiUser> loadUserByUsername(@RequestParam(name = "certificate") String certificate, @PathVariable(name = "type") Integer type, @RequestHeader("from") String from);

    /**
     * 更新登录信息
     * @param loginInfo
     * @param from
     * @return
     */
    @PutMapping("/user")
    void updateLoginInfo(Map<String,Object> loginInfo, @RequestHeader("from") String from);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @PutMapping("/user/update")
    void updateUserInfo(@RequestBody MiUser user);

    /**
     * 更新用户的积分
     * @param oldPoint/
     * @param newPoint/
     * @return
     */
    @PutMapping("/user/point")
    void updateUserPoint(@RequestParam Integer oldPoint,@RequestParam Integer newPoint);

    @GetMapping("/info/{userId}")
    MiUserDTO getUserInfo(@PathVariable("userId") Long userId);
}
