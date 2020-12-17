package org.mi.biz.tool.controller;

import cn.hutool.extra.mail.MailUtil;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.mi.api.tool.dto.EmailDTO;
import org.mi.common.core.constant.EmailConstant;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.constant.SmsMessageConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.SmsSendFailException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
import org.mi.common.core.util.RedisUtils;
import org.mi.security.annotation.Anonymous;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-14 17:46
 **/
@RestController
@RequestMapping("/tool/sms")
@RequiredArgsConstructor
public class SmsController {

    private final RocketMQTemplate rocketMQTemplate;

    private final RedisUtils redisUtils;

    /**
     *
     * @param contact 联系方式
     * @param type 0 表示发送手机短信验证码,1表示发送邮箱验证码
     * @return /
     */
    @Anonymous
    @PostMapping("/sendCode")
    public R<Void> sendCode(@RequestParam("contact") String contact, @RequestParam("type") Integer type) {
        if (type == 0) {
            AssertUtil.isPhoneNumber(contact);
            SendResult sendResult = this.rocketMQTemplate.syncSend(SmsMessageConstant.PHONE_CODE_DESTINATION, contact);
            if (!sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                throw new SmsSendFailException("系统错误消息发送失败,请稍后重试");
            }
            return R.success();
        }
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(contact);
        AssertUtil.isEmail(contact);
        SendResult sendResult = this.rocketMQTemplate.syncSend(EmailConstant.CODE_DESTINATION, emailDTO);
        if (!sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
            throw new SmsSendFailException("系统错误消息发送失败,请稍后重试");
        }
        return R.success();

    }

    @PostMapping("validate/verifyCode")
    public R<Void> validateVerifyCode(String contact, String code) {
        AssertUtil.notBlank(code);
        String cacheCode = this.redisUtils.get(RedisCacheConstant.VERIFY_CODE_PREFIX + contact).toString();
        this.redisUtils.del(RedisCacheConstant.VERIFY_CODE_PREFIX + contact);
        if (!cacheCode.equals(code)) {
            throw new IllegalParameterException("验证码不正确");
        }
        return R.success();
    }
}
