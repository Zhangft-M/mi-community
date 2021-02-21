package org.mi.post.test;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import com.alibaba.nacos.common.utils.MD5Utils;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Base64Utils;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-15 13:02
 **/
public class CommonTest {

    private static Boolean a = true;

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 5, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>()
            , new CustomizableThreadFactory("post-deal-thread-"));

    public static void main(String[] args) throws NoSuchFieldException {

        // System.out.println(StringEscapeUtils.escapeHtml("......</p>"));
        /*System.out.println(MD5Utils.md5Hex("我是谁！！！！！！！", "utf-8"));
        System.out.println("----------------------------------------");
        System.out.println(MD5Utils.md5Hex("我是谁！！！！！！！", "utf-8"));*/
        System.out.println(MD5.create().digestHex("我是谁！！！！！！！", StandardCharsets.UTF_8));

    }

    @Test
    public void threadTest() throws InterruptedException {
        int count = 0;
        this.executor.execute(() -> {
            for (int j = 0; j < 5; j++) {
                try {
                    System.out.println(Thread.currentThread().getName() + j);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        for (int i = 0; i < 10; i++) {
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName() + i);
        }
    }
}
