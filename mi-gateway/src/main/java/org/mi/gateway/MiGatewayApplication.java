package org.mi.gateway;

import cn.hutool.crypto.asymmetric.RSA;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.util.FileUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

    @Bean
    public RSA rsa(){
        String privateKey = FileUtils.readFileContent("privateKey.txt");
        AssertUtil.notBlank(privateKey);
        return new RSA(privateKey, null);
    }
}
