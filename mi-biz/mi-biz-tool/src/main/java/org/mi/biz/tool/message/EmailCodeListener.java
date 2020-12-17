package org.mi.biz.tool.message;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.mi.api.tool.dto.EmailDTO;
import org.mi.biz.tool.util.MailHelper;
import org.mi.common.core.constant.EmailConstant;
import org.mi.common.core.constant.RedisCacheConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.util.RedisUtils;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-14 18:54
 **/
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "EMAil-CODE-CONSUMER-GROUP", topic = EmailConstant.EMAIL_TOPIC,
        selectorExpression = EmailConstant.EMAIL_CODE_TAG)
public class EmailCodeListener implements RocketMQListener<EmailDTO> {

    private final RedisUtils redisUtils;

    private final MailHelper mailHelper;



    @Override
    public void onMessage(EmailDTO message) {
        AssertUtil.notBlank(message.getTo());
        String code = RandomUtil.randomNumbers(6);
        message.setContent(code);
        message.setTitle("邮箱验证码");
        this.mailHelper.sendEmail(message,"emailCode.ftl");
        this.redisUtils.set(RedisCacheConstant.VERIFY_CODE_PREFIX + message.getTo(), code, 5, TimeUnit.MINUTES);
    }
}
