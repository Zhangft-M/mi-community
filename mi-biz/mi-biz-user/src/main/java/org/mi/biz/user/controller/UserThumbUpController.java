package org.mi.biz.user.controller;

import lombok.RequiredArgsConstructor;
import org.mi.api.user.entity.UserThumbUp;
import org.mi.biz.user.service.IUserThumbUpService;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
import org.mi.security.util.SecurityContextHelper;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-22 18:11
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/thumbUp")
public class UserThumbUpController {

    private final IUserThumbUpService thumbUpService;

    @GetMapping
    public R<Set<Long>> listByUserId(){
        Long userId = SecurityContextHelper.getUserId();
        Set<Long> result = this.thumbUpService.listByUserId(userId);
        return R.success(result);
    }

    @PutMapping
    public R<Void> thumbUp(@RequestBody UserThumbUp thumbUp){
        AssertUtil.idIsNull(thumbUp.getUserId());
        thumbUp.setUserId(SecurityContextHelper.getUserId());
        this.thumbUpService.thumbUp(thumbUp);
        return R.success();
    }
}
