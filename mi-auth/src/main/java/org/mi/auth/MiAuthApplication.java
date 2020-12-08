package org.mi.auth;

import org.mi.common.core.constant.FeignApiPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-10-24 23:38
 **/
@SpringBootApplication
@EnableFeignClients(value = FeignApiPackage.USER_API_PACKAGE)
public class MiAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiAuthApplication.class,args);
    }
}
