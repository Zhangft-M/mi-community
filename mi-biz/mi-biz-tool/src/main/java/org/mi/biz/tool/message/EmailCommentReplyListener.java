package org.mi.biz.tool.message;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.mi.api.tool.dto.EmailDTO;
import org.mi.biz.tool.util.MailHelper;
import org.mi.common.core.constant.EmailConstant;
import org.mi.common.core.exception.util.AssertUtil;
import org.springframework.stereotype.Component;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-14 22:39
 **/
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "EMAil-COMMENT-REPLY-CONSUMER-GROUP",topic = EmailConstant.EMAIL_TOPIC,
        selectorExpression = EmailConstant.EMAIL_COMMENT_REPLY_TAG)
public class EmailCommentReplyListener implements RocketMQListener<EmailDTO> {

    private final MailHelper mailHelper;

    @Override
    public void onMessage(EmailDTO message) {
        AssertUtil.notNull(message);
        if (StrUtil.isBlank(message.getTo())) {
            log.info("接收邮件地址不能为空");
            return;
        }
        log.info("开始发送评论回复邮件:content=>{}",message);
        this.mailHelper.sendEmail(message,"emailCommentReply.ftl");
        log.info("评论回复邮件发送完成");
    }
}
