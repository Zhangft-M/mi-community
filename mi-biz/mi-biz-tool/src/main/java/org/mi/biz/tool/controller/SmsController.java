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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("sendCode")
    public R<Void> sendCode(String contact, Integer type) {
        if (type == 0) {
            AssertUtil.isPhoneNumber(contact);
            SendResult sendResult = this.rocketMQTemplate.syncSend(SmsMessageConstant.VERIFY_CODE_TOPIC + ":" + SmsMessageConstant.VERIFY_CODE_TAG, contact);
            if (!sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                throw new SmsSendFailException("系统错误消息发送失败,请稍后重试");
            }
            return R.success();
        }
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTos(Collections.singletonList(contact));
        AssertUtil.isEmail(contact);
        SendResult sendResult = this.rocketMQTemplate.syncSend(EmailConstant.EMAIL_TOPIC + ":" + EmailConstant.EMAIL_CODE_TAG, emailDTO);
        if (!sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
            throw new SmsSendFailException("系统错误消息发送失败,请稍后重试");
        }
        return R.success();

    }

    @PostMapping("validate/verifyCode")
    public R<Void> validateVerifyCode(String contact, String code, Integer type) {
        AssertUtil.notBlank(code);
        String cacheCode = this.redisUtils.get(RedisCacheConstant.VERIFY_CODE_PREFIX + contact).toString();
        this.redisUtils.del(RedisCacheConstant.VERIFY_CODE_PREFIX + contact);
        if (!cacheCode.equals(code)) {
            throw new IllegalParameterException("验证码不正确");
        }
        return R.success();
    }
}
