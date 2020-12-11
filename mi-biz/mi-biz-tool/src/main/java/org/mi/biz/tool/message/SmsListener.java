package org.mi.biz.tool.message;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.alicloud.sms.ISmsService;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.exceptions.ClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.constant.SmsMessageConstant;
import org.mi.common.core.exception.SmsSendFailException;
import org.mi.common.core.util.RedisUtils;
import org.springframework.stereotype.Component;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-01 14:43
 **/
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "sms-consumer",topic = SmsMessageConstant.VERIFY_CODE_TOPIC,selectorExpression = SmsMessageConstant.VERIFY_CODE_TAG)
public class SmsListener implements RocketMQListener<String> {

    private final ISmsService smsService;

    private final RedisUtils redisUtils;

    private final SendSmsRequest sendSmsRequest;


    @Override
    public void onMessage(String phoneNumber) {
        String code = RandomUtil.randomNumbers(6);
        this.sendSmsRequest.setPhoneNumbers(phoneNumber);
        this.sendSmsRequest.setTemplateParam("{\"code\":\"" + code + "\"}");
        try {
            this.smsService.sendSmsRequest(sendSmsRequest);
        } catch (ClientException e) {
            log.error("短信发送失败,{}",e.getErrMsg());
            throw new SmsSendFailException("短信发送失败");
        }
        this.redisUtils.set(RedisCacheConstant.VERIFY_CODE_PREFIX + phoneNumber,code);
        System.out.println(phoneNumber);
    }
}
