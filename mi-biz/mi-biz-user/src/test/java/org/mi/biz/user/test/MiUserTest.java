package org.mi.biz.user.test;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.mi.api.user.entity.MiUser;
import org.mi.biz.user.MiUserApplication;
import org.mi.biz.user.service.IMiUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-27 20:47
 **/
@SpringBootTest(classes = MiUserApplication.class)
public class MiUserTest {

    @Autowired
    private IMiUserService miUserService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void queryUser(){
        MiUser user = this.miUserService.loadUserByUsername("123456789", 1);
        System.out.println(user);
    }

    @Test
    public void senSms(){
        SendResult result = this.rocketMQTemplate.syncSend("sms:test", "测试消息");
        System.out.println(result);
    }

    @Test
    public void senAsyncSms(){
        this.rocketMQTemplate.asyncSend("async:test", "异步测试消息", new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult);
            }

            @Override
            public void onException(Throwable e) {
                System.out.println("发送失败" + e.getMessage());
            }
        });
    }
}
