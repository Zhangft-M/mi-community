package org.mi.biz.post.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-08 17:06
 **/
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "aliyun.text.check")
public class AliyunTextCheckConfig {

    private String accessKeyId;

    private String accessKeySecret;


    @Bean
    public IAcsClient clientProfile() {
        IClientProfile profile = DefaultProfile
                .getProfile("cn-shanghai", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-shanghai", "Green", "green.cn-shanghai.aliyuncs.com");
        return new DefaultAcsClient(profile);
    }
}
