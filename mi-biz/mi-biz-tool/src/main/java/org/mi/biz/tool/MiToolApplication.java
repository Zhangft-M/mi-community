package org.mi.biz.tool;

import org.mi.common.core.constant.FeignApiPackage;
import org.mi.security.annotation.EnableCustomFeignClient;
import org.mi.security.annotation.EnableCustomizeResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-01 16:00
 **/
@SpringBootApplication
@EnableCustomFeignClient
@EnableCustomizeResourceServer
public class MiToolApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiToolApplication.class);
    }
}
