package org.mi.biz.tool.config;

import com.alibaba.alicloud.context.AliCloudProperties;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-11 16:37
 **/
@Configuration
@RequiredArgsConstructor
public class ContentCheckConfig {

    private final AliCloudProperties aliCloudProperties;

    @Bean
    @SneakyThrows
    public IClientProfile iClientProfile(){
        IClientProfile profile = DefaultProfile
                .getProfile("cn-shanghai", this.aliCloudProperties.getAccessKey(), this.aliCloudProperties.getSecretKey());
        DefaultProfile
                .addEndpoint("cn-shanghai", "cn-shanghai", "Green", "green.cn-shanghai.aliyuncs.com");
        return profile;
    }

    @Bean
    public IAcsClient iAcsClient(){
        return new DefaultAcsClient(iClientProfile());
    }



}
