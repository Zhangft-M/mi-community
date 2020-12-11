package org.mi.biz.tool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-11 15:05
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "oss.config")
public class OssConfig {

    private Map<String,String> bucket;
}
