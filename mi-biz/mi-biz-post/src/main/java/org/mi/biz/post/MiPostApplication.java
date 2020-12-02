package org.mi.biz.post;

import org.mi.security.annotation.EnableCustomizeResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-10 13:24
 **/

@SpringBootApplication
@EnableCustomizeResourceServer
public class MiPostApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiPostApplication.class,args);
    }
}
