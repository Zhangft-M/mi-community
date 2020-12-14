package org.mi.biz.tool.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-14 18:08
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "mail.config")
public class MailConfig {

    private String host;

    private String port;

    private String user;

    private String pass;

    private String fromUser;
}
