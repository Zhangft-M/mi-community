package org.mi.biz.tool.message;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.mi.api.tool.dto.EmailDTO;
import org.mi.biz.tool.util.MailHelper;
import org.mi.common.core.constant.EmailConstant;
import org.mi.common.core.exception.util.AssertUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-14 18:03
 **/
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "EMAil-SEND-GROUP",topic = EmailConstant.EMAIL_TOPIC,
        selectorExpression = EmailConstant.EMAIL_POST_REPLY_TAG)
public class EmailPostReplyListener implements RocketMQListener<EmailDTO> {

    private final MailHelper mailHelper;

    @Override
    public void onMessage(EmailDTO message) {
        AssertUtil.notNull(message);
        this.mailHelper.sendEmail(message,"emailPostReply.ftl");
    }
}
