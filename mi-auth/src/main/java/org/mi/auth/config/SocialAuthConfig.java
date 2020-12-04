package org.mi.auth.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.mi.auth.model.AuthParams;
import org.springframework.beans.factory.InitializingBean;
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
@Data
@Configuration
@ConfigurationProperties(prefix = "auth.social")
public class SocialAuthConfig {

    private Map<AuthDefaultSource, AuthConfig> type;


}
