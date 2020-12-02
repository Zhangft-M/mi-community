package org.mi.auth.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-05 18:47
 **/
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "auth.social")
public class SocialAuthConfig {

    private Map<String,ConfigProperties> param;

    @Data
    class ConfigProperties{
        private String clientId;

        private String clientSecret;

        /**
         * 登录成功后的回调地址
         */
        private String redirectUri;
    }



    /**
     * 支付宝公钥：当选择支付宝登录时，该值可用
     */
    private String alipayPublicKey = "";

    /**
     * 是否需要申请unionid，目前只针对qq登录
     * 注：qq授权登录时，获取unionid需要单独发送邮件申请权限。如果个人开发者账号中申请了该权限，可以将该值置为true，在获取openId时就会同步获取unionId
     * 参考链接：http://wiki.connect.qq.com/unionid%E4%BB%8B%E7%BB%8D
     * <p>
     * 1.7.1版本新增参数
     */
    private boolean unionId = false;

    /**
     * Stack Overflow Key
     * <p>
     *
     * @since 1.9.0
     */
    private String stackOverflowKey = "";

    /**
     * 企业微信，授权方的网页应用ID
     *
     * @since 1.10.0
     */
    private String agentId = "";

}
