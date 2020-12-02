package org.mi.api.user.api;

import com.baomidou.mybatisplus.extension.api.R;
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
    @GetMapping("/mi-user/miUser/{type}")
    ResponseEntity<MiUser> loadUserByUsername(@RequestParam(name = "certificate") String certificate, @PathVariable(name = "type") Integer type, @RequestHeader("from") String from);

    @PutMapping("/mi-user/miUser")
    public R<Void> updateLoginInfo(Map<String,Object> loginInfo, @RequestHeader("from") String from);
}
