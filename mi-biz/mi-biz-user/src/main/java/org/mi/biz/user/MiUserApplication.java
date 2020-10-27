package org.mi.biz.user;

import org.mi.security.annotation.EnableCustomizeResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-10-25 14:58
 **/

@SpringBootApplication
@EnableCustomizeResourceServer
public class MiUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiUserApplication.class,args);
    }
}
