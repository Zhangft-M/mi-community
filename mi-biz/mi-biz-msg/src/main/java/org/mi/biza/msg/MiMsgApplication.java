package org.mi.biza.msg;

import org.mi.common.core.constant.FeignApiPackage;
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
@EnableFeignClients(basePackages = FeignApiPackage.USER_API_PACKAGE)
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MiMsgApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiMsgApplication.class);
    }
}
