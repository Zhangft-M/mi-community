package org.mi.biz.user;

import org.mi.security.annotation.EnableCustomFeignClient;
import org.mi.security.annotation.EnableCustomizeResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-10-25 14:58
 **/

@EnableCustomFeignClient
@SpringBootApplication
@EnableCustomizeResourceServer
public class MiUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiUserApplication.class,args);
    }

}
