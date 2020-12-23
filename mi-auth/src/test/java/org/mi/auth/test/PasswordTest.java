package org.mi.auth.test;

import org.junit.jupiter.api.Test;
import org.mi.auth.MiAuthApplication;
import org.mi.auth.config.SocialAuthConfig;
import org.mi.auth.model.AuthParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-25 13:10
 **/
@SpringBootTest(classes = MiAuthApplication.class)
public class PasswordTest {

    @Autowired
    private SocialAuthConfig socialAuthConfig;

    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("weixiaohan"));
    }

}
