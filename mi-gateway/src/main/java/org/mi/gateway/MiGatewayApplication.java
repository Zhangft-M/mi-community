package org.mi.gateway;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-03 18:00
 **/
@SpringBootApplication
@MapperScan("org.mi.gateway.mapper")
public class MiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiGatewayApplication.class,args);
    }
}
