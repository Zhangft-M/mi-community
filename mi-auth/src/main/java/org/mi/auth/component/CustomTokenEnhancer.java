package org.mi.auth.component;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.mi.auth.model.MiUserInfo;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.constant.UserMessageConstant;
import org.mi.common.core.exception.SmsSendFailException;
import org.mi.common.core.util.DateUtil;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-25 16:06
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomTokenEnhancer implements TokenEnhancer {

    private final RocketMQTemplate rocketMQTemplate;

    private final HttpServletRequest request;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken auth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
        Map<String, Object> info = new LinkedHashMap<>(accessToken.getAdditionalInformation());
        MiUserInfo user = (MiUserInfo) authentication.getPrincipal();
        info.put(MiUserConstant.USER_NAME,user.getUsername());
        info.put(MiUserConstant.USER_ID,user.getUserId());
        auth2AccessToken.setAdditionalInformation(info);
        // 发送异步消息,保存用户登录信息
        String requestIp = authentication.getOAuth2Request().getRequestParameters().get("requestIp");

        Map<String,Object> messageMap = new HashMap<>();
        messageMap.put(MiUserConstant.USER_ID,user.getUserId());
        messageMap.put(MiUserConstant.LOGIN_IP,this.request.getRemoteAddr());
        messageMap.put(MiUserConstant.LOGIN_TIME, LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        this.rocketMQTemplate.asyncSend(UserMessageConstant.SAVE_LOGIN_INFO_TOPIC + ":" + UserMessageConstant.SAVE_LOGIN_INFO_TAG,
                messageMap, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)){
                            log.info("发送成功");
                        }
                    }
                    @Override
                    public void onException(Throwable e) {
                        throw new SmsSendFailException("发送更新用户登录ip与时间信息失败");
                    }
                });
        return auth2AccessToken;
    }
}
