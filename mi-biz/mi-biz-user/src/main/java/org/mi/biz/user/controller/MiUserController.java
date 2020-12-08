package org.mi.biz.user.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.mi.api.user.dto.MiUserDTO;
import org.mi.api.user.entity.MiUser;
import org.mi.api.user.mapstruct.MiUserMapStruct;
import org.mi.biz.user.service.IMiUserService;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.SmsMessageConstant;
import org.mi.common.core.exception.SmsSendFailException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
import org.mi.security.annotation.Inner;
import org.mi.security.util.SecurityContextHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
        // Long userId = SecurityContextHelper.getUserId();
        return R.success(this.miUserService.getUserInfo(userId));
    }

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

    @GetMapping("sendSms")
    public R<Void> senSms(String phone){
        AssertUtil.isPhoneNumber(phone);
        SendResult sendResult = this.rocketMQTemplate.syncSend(SmsMessageConstant.VERIFY_CODE_TOPIC + ":" + SmsMessageConstant.VERIFY_CODE_TAG, phone);
        if (!sendResult.getSendStatus().equals(SendStatus.SEND_OK)){
            throw new SmsSendFailException("系统错误消息发送失败,请稍后重试");
        }
        return R.success();
    }
}
