package org.mi.biza.msg.listener;

import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.mi.api.user.api.MiUserRemoteApi;
import org.mi.api.user.entity.MiUser;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.common.core.constant.UserMessageConstant;
import org.mi.common.core.util.DateUtil;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-02 11:45
 **/
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "USER-INFO-GROUP",topic = UserMessageConstant.SAVE_LOGIN_INFO_TOPIC,selectorExpression = UserMessageConstant.SAVE_LOGIN_INFO_TAG)
public class UserInfoLoginListener implements RocketMQListener<Map<String,Object>> {

    private final MiUserRemoteApi miUserRemoteApi;


    /**
     *
     * @param message
     */
    @Override
    public void onMessage(Map<String, Object> message) {
        this.miUserRemoteApi.updateLoginInfo(message, SecurityConstant.FROM_IN);
    }
}
