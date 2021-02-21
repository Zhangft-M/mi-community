package org.mi.biz.tool.config;

import com.alibaba.alicloud.context.AliCloudProperties;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.tencentcloudapi.cms.v20190321.CmsClient;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
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


    @Bean
    public CmsClient cmsClient(){
        // 腾讯内容检测配置
        Credential cred = new Credential("",
                "");
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("cms.tencentcloudapi.com");
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        return new CmsClient(cred, "ap-guangzhou", clientProfile);
    }

}
