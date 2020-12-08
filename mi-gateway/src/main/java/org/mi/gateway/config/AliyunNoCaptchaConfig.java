package org.mi.gateway.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigRequest;
import com.aliyuncs.profile.DefaultProfile;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: eladmin-cloud
 * @description: 阿里云安全验证配置
 * @author: Micah
 * @create: 2020-10-27 15:50
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.captcha")
public class AliyunNoCaptchaConfig {

    private String regionId;

    private String accessKeyId;

    private String accessKeySecret;

    private String appKey;

    @Bean
    public IAcsClient acsClient(){
        DefaultProfile.addEndpoint(this.getRegionId(),"afs","afs.aliyuncs.com");
        DefaultProfile profile = DefaultProfile.getProfile(this.getRegionId(), this.getAccessKeyId(), this.getAccessKeySecret());
        return new DefaultAcsClient(profile);
    }

    @Bean
    public AuthenticateSigRequest authenticateSigRequest(){
        AuthenticateSigRequest authenticateSigRequest = new AuthenticateSigRequest();
        authenticateSigRequest.setAppKey(this.getAppKey());
        return authenticateSigRequest;
    }



}
