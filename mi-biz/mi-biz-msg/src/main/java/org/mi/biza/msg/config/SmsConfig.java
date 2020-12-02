package org.mi.biza.msg.config;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-01 19:26
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "sms.config")
public class SmsConfig {

    private String signName;

    private String templateCode;


    @Bean
    public SendSmsRequest sendSmsRequest(){
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setSignName(signName);
        sendSmsRequest.setTemplateCode(templateCode);
        return sendSmsRequest;
    }

}
